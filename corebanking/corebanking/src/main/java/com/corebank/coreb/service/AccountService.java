package com.corebank.coreb.service;

import com.corebank.coreb.dto.AccountDTO;
import com.corebank.coreb.entity.Account;
import com.corebank.coreb.entity.Branch;
import com.corebank.coreb.entity.Customer;
import com.corebank.coreb.entity.Deposit;
import com.corebank.coreb.entity.SystemDate;
import com.corebank.coreb.repository.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private SystemDateRepository systemDateRepository;

    // --------------------
    // Convert Account -> AccountDTO
    // --------------------
    public AccountDTO toDTO(Account account) {
        AccountDTO dto = new AccountDTO();

        dto.setAccountId(account.getAccountId());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());

        // --- Customer ---
        if (account.getCustomer() != null) {
            dto.setCustomer(account.getCustomer().getCustomerId());
            dto.setCustomerName(
                account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName()
            );
        }

        // --- Branch ---
        if (account.getBranch() != null) {
            dto.setBranch(account.getBranch().getBranchId());
            dto.setBranchName(account.getBranch().getName());
        }

        return dto;
    }
    
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    // --------------------
    // Get current system date
    // --------------------
    private LocalDate getSystemDate() {
        return systemDateRepository.findAll().stream()
                .findFirst()
                .map(SystemDate::getCurrentDate)
                .orElse(LocalDate.now());
    }

    // --------------------
    // Save or update account
    // --------------------
    public Account saveAccount(Account account) {
        if (account.getBalance() == null) account.setBalance(BigDecimal.ZERO);
        if (account.getStatus() == null) account.setStatus("Active");
        return accountRepository.save(account);
    }

    // --------------------
    // Get all accounts as DTOs
    // --------------------
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // --------------------
    // Deposit money
    // --------------------
    @Transactional
    public Deposit deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Deposit amount must be positive");

        account.setBalance(account.getBalance().add(amount));

        Deposit deposit = new Deposit();
        deposit.setAccount(account);
        deposit.setCustomer(account.getCustomer());
        deposit.setPrincipalAmount(amount);
        deposit.setDepositType("DEPOSIT");
        deposit.setStartDate(getSystemDate());
        deposit.setStatus("ACTIVE");

        accountRepository.save(account);
        return depositRepository.save(deposit);
    }

    // --------------------
    // Withdraw money
    // --------------------
    @Transactional
    public Deposit withdraw(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive");

        if (account.getBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Insufficient balance");

        account.setBalance(account.getBalance().subtract(amount));

        Deposit deposit = new Deposit();
        deposit.setAccount(account);
        deposit.setCustomer(account.getCustomer());
        deposit.setPrincipalAmount(amount);
        deposit.setDepositType("WITHDRAWAL");
        deposit.setStartDate(getSystemDate());
        deposit.setStatus("ACTIVE");

        accountRepository.save(account);
        return depositRepository.save(deposit);
    }

    // --------------------
    // Calculate monthly interest
    // --------------------
    private BigDecimal calculateMonthlyInterest(Account account) {
        if (!"SAVINGS".equalsIgnoreCase(account.getAccountType()) || account.getBalance() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal annualRate = new BigDecimal("0.05"); // 5%
        BigDecimal monthlyRate = annualRate.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        return account.getBalance().multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
    }

    // --------------------
    // Apply monthly interest
    // --------------------
    @Transactional
    public Deposit applyMonthlyInterest(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        BigDecimal interest = calculateMonthlyInterest(account);
        if (interest.compareTo(BigDecimal.ZERO) <= 0) return null;

        account.setBalance(account.getBalance().add(interest));

        Deposit deposit = new Deposit();
        deposit.setAccount(account);
        deposit.setCustomer(account.getCustomer());
        deposit.setPrincipalAmount(interest);
        deposit.setDepositType("INTEREST");
        deposit.setInterestRate(new BigDecimal("0.05"));
        deposit.setStartDate(getSystemDate());
        deposit.setStatus("ACTIVE");

        accountRepository.save(account);
        return depositRepository.save(deposit);
    }

    // --------------------
    // Deactivate account
    // --------------------
    @Transactional
    public boolean deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus("Inactive");
        accountRepository.save(account);

        depositRepository.deactivateDepositsByAccountId(accountId);
        cardRepository.deactivateCardsByAccountId(accountId);
        return true;
    }

    // --------------------
    // Close account
    // --------------------
    @Transactional
    public Account closeAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus("Closed");
        accountRepository.save(account);

        depositRepository.deactivateDepositsByAccountId(accountId);
        cardRepository.deactivateCardsByAccountId(accountId);
        return account;
    }

    // --------------------
    // Safe delete
    // --------------------
    @Transactional
    public boolean safeDeleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!"Inactive".equalsIgnoreCase(account.getStatus()) &&
                !"Closed".equalsIgnoreCase(account.getStatus())) {
            return false;
        }

        depositRepository.deleteByAccountId(accountId);
        cardRepository.deleteByAccountId(accountId);
        accountRepository.delete(account);
        return true;
    }
}
