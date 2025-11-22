package com.corebank.coreb.controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    // Convert Entity → DTO (fixed to include names)
    // --------------------
    private AccountDTO mapToDTO(Account account) {
        AccountDTO dto = new AccountDTO();

        dto.setAccountId(account.getAccountId());
        dto.setAccountType(account.getAccountType());
        dto.setBalance(account.getBalance());
        dto.setStatus(account.getStatus());

        // ---- Customer ----
        if (account.getCustomer() != null) {
            dto.setCustomer(account.getCustomer().getCustomerId());
            dto.setCustomerName(
                account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName()
            );
        }

        // ---- Branch ----
        if (account.getBranch() != null) {
            dto.setBranch(account.getBranch().getBranchId());
            dto.setBranchName(account.getBranch().getName());
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
        List<AccountDTO> dtos = accountService.getAllAccounts();
        return ResponseEntity.ok(dtos);
    }

    // --------------------
    // Get account by ID
    // --------------------
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getAccountById(@PathVariable Long id) {
        Optional<Account> accountOpt = accountService.getAccountById(id);

        return accountOpt
                .map(account -> ResponseEntity.ok(mapToDTO(account)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --------------------
    // Update account
    // --------------------
    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> updateAccount(
            @PathVariable Long id,
            @RequestBody Account accountDetails) {

        Optional<Account> accountOpt = accountService.getAccountById(id);

        if (accountOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

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
        if (deposit == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(deposit);
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
        if (!success) return ResponseEntity.notFound().build();
        return ResponseEntity.ok("Account deactivated successfully.");
    }

    // --------------------
    // Safe delete
    // --------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> safeDeleteAccount(@PathVariable Long id) {
        boolean deleted = accountService.safeDeleteAccount(id);

        if (!deleted) {
            return ResponseEntity.badRequest()
                    .body("Cannot delete account: either not found or active.");
        }

        return ResponseEntity.ok("Account deleted successfully.");
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
