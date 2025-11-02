package com.corebank.coreb.service;

import com.corebank.coreb.dto.CustomerDTO;
import com.corebank.coreb.entity.Account;
import com.corebank.coreb.entity.Branch;
import com.corebank.coreb.entity.Customer;
import com.corebank.coreb.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private BranchRepository branchRepository;

    // --------------------
    // Create Customer
    // --------------------
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        mapToEntity(customerDTO, customer);

        if (customer.getStatus() == null) {
            customer.setStatus("Active");
        }
        customer.setCreatedAt(LocalDateTime.now());

        Branch branch = branchRepository.findById(customerDTO.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        customer.setBranch(branch);

        Customer saved = customerRepository.save(customer);
        return mapToDTO(saved);
    }

    // --------------------
    // Update Customer
    // --------------------
    public CustomerDTO updateCustomer(Long customerId, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        mapToEntity(customerDTO, existingCustomer);

        Branch branch = branchRepository.findById(customerDTO.getBranchId())
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        existingCustomer.setBranch(branch);

        Customer updated = customerRepository.save(existingCustomer);
        return mapToDTO(updated);
    }

    // --------------------
    // Get by ID
    // --------------------
    public Optional<CustomerDTO> getCustomerById(Long customerId) {
        return customerRepository.findById(customerId)
                .map(this::mapToDTO);
    }

    // --------------------
    // Get All
    // --------------------
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --------------------
    // Deactivate Customer (Soft Delete)
    // --------------------
    public boolean deactivateCustomer(Long customerId) {
        return evaluateCustomerStatus(customerId);
    }

    // --------------------
    // Safe Delete Customer (Hard Delete)
    // --------------------
    public boolean safeDeleteCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        boolean hasActiveLoans = loanRepository.existsByCustomerAndStatus(customer, "Active");
        if (hasActiveLoans) return false;

        List<Account> accounts = accountRepository.findByCustomer(customer);
        boolean hasActiveAccounts = accounts.stream()
                .anyMatch(a -> "Active".equalsIgnoreCase(a.getStatus()));
        if (hasActiveAccounts) return false;

        for (Account account : accounts) {
            accountService.safeDeleteAccount(account.getAccountId());
        }

        customerRepository.delete(customer);
        return true;
    }

    // --------------------
    // Evaluate and Update Status
    // --------------------
    public boolean evaluateCustomerStatus(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        boolean hasActiveLoans = loanRepository.existsByCustomerAndStatus(customer, "Active");
        boolean hasActiveAccounts = accountRepository.existsByCustomerAndStatus(customer, "Active");

        if (!hasActiveLoans && !hasActiveAccounts) {
            customer.setStatus("Inactive");
            customerRepository.save(customer);
            return true;
        }

        if (!"Active".equalsIgnoreCase(customer.getStatus())) {
            customer.setStatus("Active");
            customerRepository.save(customer);
        }
        return false;
    }

    // --------------------
    // Mapping Helpers
    // --------------------
    private CustomerDTO mapToDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setFirstName(customer.getFirstName());
        dto.setLastName(customer.getLastName());
        dto.setEmail(customer.getEmail());
        dto.setPhone(customer.getPhone());
        dto.setDob(customer.getDob());
        dto.setAddressLine1(customer.getAddressLine1());
        dto.setCity(customer.getCity());
        dto.setState(customer.getState());
        dto.setZip(customer.getZip());
        dto.setKycDetails(customer.getKycDetails());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setStatus(customer.getStatus());
        dto.setBranchId(customer.getBranch() != null ? customer.getBranch().getBranchId() : null);
        dto.setBranchName(customer.getBranch() != null ? customer.getBranch().getName() : null);
        return dto;
    }

    private void mapToEntity(CustomerDTO dto, Customer customer) {
        customer.setFirstName(dto.getFirstName());
        customer.setLastName(dto.getLastName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setDob(dto.getDob());
        customer.setAddressLine1(dto.getAddressLine1());
        customer.setCity(dto.getCity());
        customer.setState(dto.getState());
        customer.setZip(dto.getZip());
        customer.setKycDetails(dto.getKycDetails());
        customer.setStatus(dto.getStatus());
    }
}
