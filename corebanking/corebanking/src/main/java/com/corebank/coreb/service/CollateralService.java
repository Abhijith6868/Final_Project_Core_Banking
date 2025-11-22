package com.corebank.coreb.service;

import com.corebank.coreb.dto.CollateralDTO;
import com.corebank.coreb.entity.Collateral;
import com.corebank.coreb.entity.Customer;
import com.corebank.coreb.entity.Loan;
import com.corebank.coreb.entity.SystemDate;
import com.corebank.coreb.repository.CollateralRepository;
import com.corebank.coreb.repository.CustomerRepository;
import com.corebank.coreb.repository.LoanRepository;
import com.corebank.coreb.repository.SystemDateRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CollateralService {

    @Autowired
    private CollateralRepository collateralRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SystemDateRepository systemDateRepository;

    // --------------------
    // Get Centralized System Date
    // --------------------
    private LocalDate getSystemDate() {
        return systemDateRepository.findAll()
                .stream()
                .findFirst()
                .map(SystemDate::getCurrentDate)
                .orElse(LocalDate.now());
    }

    // --------------------
    // Create Collateral
    // --------------------
    public CollateralDTO saveCollateral(CollateralDTO collateralDTO) {
        Collateral collateral = new Collateral();
        mapToEntity(collateralDTO, collateral);

        Loan loan = loanRepository.findById(collateralDTO.getLoanId())
                .orElseThrow(() -> new RuntimeException("Loan not found"));
        Customer customer = customerRepository.findById(collateralDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        collateral.setLoan(loan);
        collateral.setCustomer(customer);

        if (collateral.getStatus() == null) {
            collateral.setStatus("Active");
        }

        // ✅ Use centralized system date for pledged date if not provided
        if (collateral.getPledgedDate() == null) {
            collateral.setPledgedDate(getSystemDate());
        }

        Collateral saved = collateralRepository.save(collateral);

        // --------------------
        // Reduce loan interest by 1% if collateral is added
        // --------------------
        BigDecimal currentRate = loan.getInterestRate();
        BigDecimal newRate = currentRate.subtract(BigDecimal.valueOf(1.0));
        loan.setInterestRate(newRate.max(BigDecimal.ZERO)); // prevent negative rate
        loanRepository.save(loan);

        return mapToDTO(saved);
    }

    // --------------------
    // Update Collateral
    // --------------------
    public CollateralDTO updateCollateral(Long collateralId, CollateralDTO collateralDTO) {
        Collateral existingCollateral = collateralRepository.findById(collateralId)
                .orElseThrow(() -> new RuntimeException("Collateral not found"));

        // Prevent changing the loan
        if (collateralDTO.getLoanId() != null &&
            !existingCollateral.getLoan().getLoanId().equals(collateralDTO.getLoanId())) {
            throw new RuntimeException("Cannot change the loan of an existing collateral");
        }

        // Prevent changing the customer
        if (!existingCollateral.getCustomer().getCustomerId().equals(collateralDTO.getCustomerId())) {
            throw new RuntimeException("Cannot change the customer of an existing collateral");
        }

        // Map other fields
        mapToEntity(collateralDTO, existingCollateral);

        // Use system date if pledgedDate is missing
        if (existingCollateral.getPledgedDate() == null) {
            existingCollateral.setPledgedDate(getSystemDate());
        }

        if (existingCollateral.getStatus() == null) {
            existingCollateral.setStatus("Active");
        }

        Collateral updated = collateralRepository.save(existingCollateral);

        return mapToDTO(updated);
    }

    // --------------------
    // Get Collateral by ID
    // --------------------
    public Optional<CollateralDTO> getCollateralById(Long collateralId) {
        return collateralRepository.findById(collateralId)
                .map(this::mapToDTO);
    }

    // --------------------
    // Get All Collaterals
    // --------------------
    public List<CollateralDTO> getAllCollaterals() {
        return collateralRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --------------------
    // Deactivate Collateral (Soft Delete)
    // --------------------
    public boolean deactivateCollateral(Long collateralId) {
        Collateral collateral = collateralRepository.findById(collateralId)
                .orElseThrow(() -> new RuntimeException("Collateral not found"));

        collateral.setStatus("Inactive");
        collateralRepository.save(collateral);
        return true;
    }

    // --------------------
    // Safe Delete Collateral (Hard Delete)
    // --------------------
    public boolean safeDeleteCollateral(Long collateralId) {
        Collateral collateral = collateralRepository.findById(collateralId)
                .orElseThrow(() -> new RuntimeException("Collateral not found"));

        if (!"Inactive".equalsIgnoreCase(collateral.getStatus())) {
            return false;
        }

        collateralRepository.delete(collateral);
        return true;
    }

    // --------------------
    // Mapping Helpers
    // --------------------
    private CollateralDTO mapToDTO(Collateral collateral) {
        CollateralDTO dto = new CollateralDTO();
        dto.setCollateralId(collateral.getCollateralId());
        dto.setCollateralType(collateral.getCollateralType());
        dto.setDescription(collateral.getDescription());
        dto.setEstimatedValue(collateral.getEstimatedValue());
        dto.setPledgedDate(collateral.getPledgedDate());
        dto.setStatus(collateral.getStatus());

        if (collateral.getLoan() != null) {
            dto.setLoanId(collateral.getLoan().getLoanId());
            dto.setLoanNumber(collateral.getLoan().getLoanNo());
        }

        if (collateral.getCustomer() != null) {
            dto.setCustomerId(collateral.getCustomer().getCustomerId());
            dto.setCustomerName(collateral.getCustomer().getFirstName() + " " + collateral.getCustomer().getLastName());
        }

        return dto;
    }

    private void mapToEntity(CollateralDTO dto, Collateral collateral) {
        collateral.setCollateralType(dto.getCollateralType());
        collateral.setDescription(dto.getDescription());
        collateral.setEstimatedValue(dto.getEstimatedValue());
        collateral.setPledgedDate(dto.getPledgedDate());
        collateral.setStatus(dto.getStatus());
    }
}
