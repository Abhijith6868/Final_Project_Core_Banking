package com.corebank.coreb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.corebank.coreb.dto.LoanDTO;
import com.corebank.coreb.entity.*;
import com.corebank.coreb.repository.*;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private RepaymentRepository repaymentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private CollateralRepository collateralRepository;

    // --------------------
    // Create Loan
    // --------------------
    public LoanDTO saveLoan(LoanDTO loanDTO) {
        Loan loan = toEntity(loanDTO);

        loan.setStatus("Pending");
        loan.setMaturityDate(loan.getStartDate().plusMonths(loan.getTenureMonths()));
        loan.setBalancePrincipal(loan.getPrincipal()); // Initialize remaining principal

        // Save loan first to generate ID
        Loan savedLoan = loanRepository.save(loan);

        return toDTO(savedLoan);
    }

    
    public LoanDTO approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        if (!"Pending".equalsIgnoreCase(loan.getStatus())) {
            throw new RuntimeException("Only pending loans can be approved");
        }

        loan.setStatus("Active");
        loan.setBalancePrincipal(loan.getPrincipal()); // Ensure balancePrincipal is correct
        Loan approvedLoan = loanRepository.save(loan);

        generateRepayments(approvedLoan);

        return toDTO(approvedLoan);
    }

    // --------------------
    // Update Loan
    // --------------------
    public LoanDTO updateLoan(Long loanId, LoanDTO loanDTO) {
        Loan existingLoan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        existingLoan.setPrincipal(loanDTO.getPrincipal());
        existingLoan.setInterestRate(loanDTO.getInterestRate());
        existingLoan.setTenureMonths(loanDTO.getTenureMonths());
        existingLoan.setStartDate(loanDTO.getStartDate());
        existingLoan.setMaturityDate(loanDTO.getStartDate().plusMonths(loanDTO.getTenureMonths()));
        existingLoan.setLoanType(loanDTO.getLoanType());
        existingLoan.setStatus(loanDTO.getStatus());
        existingLoan.setBalancePrincipal(loanDTO.getPrincipal()); // Update balance if principal changes

        if (loanDTO.getCollateralId() != null) {
            Collateral collateral = collateralRepository.findById(loanDTO.getCollateralId())
                    .orElse(null);
            existingLoan.setCollateral(collateral);
        }

        Branch branch = branchRepository.findById(loanDTO.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        Customer customer = customerRepository.findById(loanDTO.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        existingLoan.setBranch(branch);
        existingLoan.setCustomer(customer);

        Loan updatedLoan = loanRepository.save(existingLoan);
        return toDTO(updatedLoan);
    }

    // --------------------
    // Get Loan by ID
    // --------------------
    public Optional<LoanDTO> getLoanById(Long loanId) {
        return loanRepository.findById(loanId).map(this::toDTO);
    }

    // --------------------
    // Get All Loans
    // --------------------
    public List<LoanDTO> getAllLoans() {
        return loanRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    // --------------------
    // Deactivate Loan
    // --------------------
    public boolean deactivateLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        loan.setStatus("Inactive");
        loanRepository.save(loan);
        repaymentRepository.deactivateRepaymentsByLoanId(loanId);
        return true;
    }

    // --------------------
    // Safe Delete Loan
    // --------------------
    public boolean safeDeleteLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new RuntimeException("Loan not found"));

        String status = loan.getStatus();
        if (!"Inactive".equalsIgnoreCase(status) && !"Closed".equalsIgnoreCase(status)) {
            return false;
        }

        repaymentRepository.deleteByLoanId(loanId);
        loanRepository.delete(loan);
        return true;
    }

    // --------------------
    // Generate Repayment Schedule (Dynamic Interest & Principal)
    // --------------------
    private void generateRepayments(Loan loan) {
        int tenure = loan.getTenureMonths();
        BigDecimal principal = loan.getPrincipal();
        BigDecimal remainingBalance = loan.getBalancePrincipal();

        BigDecimal monthlyRate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

        LocalDate startDate = loan.getStartDate();

        for (int i = 1; i <= tenure; i++) {
            Repayment repayment = new Repayment();
            repayment.setLoan(loan);
            repayment.setCustomer(loan.getCustomer());
            repayment.setDueDate(startDate.plusMonths(i));
            repayment.setRateOfInterest(loan.getInterestRate());

            BigDecimal interestDue = remainingBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalDue = principal.divide(BigDecimal.valueOf(tenure), 2, RoundingMode.HALF_UP);
            BigDecimal totalDue = principalDue.add(interestDue);

            repayment.setExpectedInterest(interestDue);
            repayment.setExpectedPrincipal(principalDue);
            repayment.setTotalDue(totalDue);

            repayment.setAmountPaid(BigDecimal.ZERO);
            repayment.setPrincipalPaid(BigDecimal.ZERO);
            repayment.setInterestPaid(BigDecimal.ZERO);
            repayment.setRemainingPrincipal(remainingBalance);
            repayment.setOutstandingInterest(BigDecimal.ZERO);
            repayment.setStatus("UNPAID");
            repayment.setBillingDone(false);

            repaymentRepository.save(repayment);

            remainingBalance = remainingBalance.subtract(principalDue);
        }
    }

    // --------------------
    // DTO ↔ Entity conversion
    // --------------------
    private LoanDTO toDTO(Loan loan) {
        LoanDTO dto = new LoanDTO();
        dto.setLoanId(loan.getLoanId());
        dto.setLoanNo(loan.getLoanNo());
        dto.setLoanType(loan.getLoanType());
        dto.setPrincipal(loan.getPrincipal());
        dto.setInterestRate(loan.getInterestRate());
        dto.setTenureMonths(loan.getTenureMonths());
        dto.setStartDate(loan.getStartDate());
        dto.setMaturityDate(loan.getMaturityDate());
        dto.setStatus(loan.getStatus());

        if (loan.getCustomer() != null) {
            dto.setCustomerId(loan.getCustomer().getCustomerId());
            dto.setCustomerName(loan.getCustomer().getFirstName() + " " + loan.getCustomer().getLastName());
        }
        if (loan.getBranch() != null) {
            dto.setBranchId(loan.getBranch().getBranchId());
            dto.setBranchName(loan.getBranch().getName());
        }
        if (loan.getCollateral() != null) {
            dto.setCollateralId(loan.getCollateral().getCollateralId());
        }
        return dto;
    }

    private Loan toEntity(LoanDTO dto) {
        Loan loan = new Loan();
        loan.setLoanId(dto.getLoanId());
        loan.setLoanNo(dto.getLoanNo());
        loan.setLoanType(dto.getLoanType());
        loan.setPrincipal(dto.getPrincipal());
        loan.setInterestRate(dto.getInterestRate());
        loan.setTenureMonths(dto.getTenureMonths());
        loan.setStartDate(dto.getStartDate());
        loan.setMaturityDate(dto.getMaturityDate());
        loan.setStatus(dto.getStatus());
        loan.setBalancePrincipal(dto.getPrincipal()); // Initialize balancePrincipal

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Branch branch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        loan.setCustomer(customer);
        loan.setBranch(branch);

        if (dto.getCollateralId() != null) {
            Collateral collateral = collateralRepository.findById(dto.getCollateralId())
                    .orElse(null);
            loan.setCollateral(collateral);
        }

        return loan;
    }
}
