package com.corebank.coreb.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.corebank.coreb.dto.AccountDTO;
import com.corebank.coreb.entity.Account;
import com.corebank.coreb.entity.Deposit;
import com.corebank.coreb.service.AccountService;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // --------------------
    // Map Account → DTO
    // --------------------
    private AccountDTO mapToDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setAccountId(account.getAccountId());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());
        if (account.getCustomer() != null) {
            dto.setCustomerId(account.getCustomer().getCustomerId());
        }
        if (account.getBranch() != null) {
            dto.setBranchId(account.getBranch().getBranchId());
        }
        return dto;
    }

    // --------------------
    // Create account
    // --------------------
    @PostMapping
    public ResponseEntity<AccountDTO> createAccount(@RequestBody Account account) {
        Account savedAccount = accountService.saveAccount(account);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToDTO(savedAccount));
    }

    // --------------------
    // Get all accounts
    // --------------------
    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        List<AccountDTO> dtos = accountService.getAllAccounts().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // --------------------
    // Get account by ID
    // --------------------
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        Optional<Account> accountOpt = accountService.getAccountById(id);
        return accountOpt.map(account -> ResponseEntity.ok(mapToDTO(account)))
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --------------------
    // Update account
    // --------------------
    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long id, @RequestBody Account accountDetails) {
        Optional<Account> accountOpt = accountService.getAccountById(id);
        if (accountOpt.isPresent()) {
            Account account = accountOpt.get();
            account.setAccountType(accountDetails.getAccountType());
            account.setBalance(accountDetails.getBalance());
            account.setStatus(accountDetails.getStatus());
            account.setCustomer(accountDetails.getCustomer());
            account.setBranch(accountDetails.getBranch());
            account.setCard(accountDetails.getCard());
            account.setCreatedAt(accountDetails.getCreatedAt());

            Account updated = accountService.saveAccount(account);
            return ResponseEntity.ok(mapToDTO(updated));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --------------------
    // Deposit money
    // --------------------
    @PostMapping("/{id}/deposit")
    public ResponseEntity<Deposit> deposit(@PathVariable Long id, @RequestBody AmountRequest request) {
        Deposit deposit = accountService.deposit(id, request.getAmount());
        return ResponseEntity.ok(deposit);
    }

    // --------------------
    // Withdraw money
    // --------------------
    @PostMapping("/{id}/withdraw")
    public ResponseEntity<Deposit> withdraw(@PathVariable Long id, @RequestBody AmountRequest request) {
        Deposit deposit = accountService.withdraw(id, request.getAmount());
        return ResponseEntity.ok(deposit);
    }

    // --------------------
    // Apply monthly interest
    // --------------------
    @PostMapping("/{id}/interest")
    public ResponseEntity<Deposit> applyInterest(@PathVariable Long id) {
        Deposit deposit = accountService.applyMonthlyInterest(id);
        if (deposit != null) {
            return ResponseEntity.ok(deposit);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    // --------------------
    // Close account
    // --------------------
    @PutMapping("/{id}/close")
    public ResponseEntity<AccountDTO> closeAccount(@PathVariable Long id) {
        Account closed = accountService.closeAccount(id);
        return ResponseEntity.ok(mapToDTO(closed));
    }

    // --------------------
    // Deactivate account
    // --------------------
    @PatchMapping("/deactivate/{id}")
    public ResponseEntity<String> deactivateAccount(@PathVariable Long id) {
        boolean success = accountService.deactivateAccount(id);
        if (success) return ResponseEntity.ok("Account deactivated successfully.");
        else return ResponseEntity.notFound().build();
    }

    // --------------------
    // Safe delete
    // --------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> safeDeleteAccount(@PathVariable Long id) {
        boolean deleted = accountService.safeDeleteAccount(id);
        if (deleted) return ResponseEntity.ok("Account deleted successfully.");
        else return ResponseEntity.badRequest()
                .body("Cannot delete account: either not found or still active.");
    }

    // --------------------
    // DTO for amount requests
    // --------------------
    public static class AmountRequest {
        private java.math.BigDecimal amount;

        public java.math.BigDecimal getAmount() { return amount; }
        public void setAmount(java.math.BigDecimal amount) { this.amount = amount; }
    }
}
