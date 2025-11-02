package com.corebank.coreb.service;

import com.corebank.coreb.entity.Account;
import com.corebank.coreb.entity.Deposit;
import com.corebank.coreb.repository.AccountRepository;
import com.corebank.coreb.repository.CardRepository;
//import com.corebank.coreb.repository.ChequeBookRepository;
import com.corebank.coreb.repository.DepositRepository;

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
    private CardRepository cardRepository;

//    @Autowired
//    private ChequeBookRepository chequeBookRepository;

    @Autowired
    private DepositRepository depositRepository;

    // --------------------
    // Save or update account
    // --------------------
    public Account saveAccount(Account account) {
        if (account.getBalance() == null) account.setBalance(BigDecimal.ZERO);
        if (account.getStatus() == null) account.setStatus("Active");
        return accountRepository.save(account);
    }

    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // --------------------
    // Deposit money
    // --------------------
    @Transactional
    public Deposit deposit(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        account.setBalance(account.getBalance().add(amount));

        Deposit deposit = new Deposit();
        deposit.setAccount(account);
        deposit.setCustomer(account.getCustomer());
        deposit.setPrincipalAmount(amount);
        deposit.setDepositType("DEPOSIT");
        deposit.setStartDate(LocalDate.now());
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

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));

        Deposit deposit = new Deposit();
        deposit.setAccount(account);
        deposit.setCustomer(account.getCustomer());
        deposit.setPrincipalAmount(amount);
        deposit.setDepositType("WITHDRAWAL");
        deposit.setStartDate(LocalDate.now());
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
        BigDecimal annualRate = new BigDecimal("0.05"); // 5% annual
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
        deposit.setStartDate(LocalDate.now());
        deposit.setStatus("ACTIVE");

        accountRepository.save(account);
        return depositRepository.save(deposit);
    }

    // --------------------
    // Soft delete account
    // --------------------
    @Transactional
    public boolean deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setStatus("Inactive");
        accountRepository.save(account);

        depositRepository.deactivateDepositsByAccountId(accountId);
        cardRepository.deactivateCardsByAccountId(accountId);
//        chequeBookRepository.deactivateChequeBooksByAccountId(accountId);

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
//        chequeBookRepository.deactivateChequeBooksByAccountId(accountId);

        return account;
    }

    // --------------------
    // Safe delete account
    // --------------------
    @Transactional
    public boolean safeDeleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        String status = account.getStatus();
        if (!"Inactive".equalsIgnoreCase(status) && !"Closed".equalsIgnoreCase(status)) {
            return false;
        }

        depositRepository.deleteByAccountId(accountId);
        cardRepository.deleteByAccountId(accountId);
//        chequeBookRepository.deleteByAccountId(accountId);

        accountRepository.delete(account);
        return true;
    }
}
