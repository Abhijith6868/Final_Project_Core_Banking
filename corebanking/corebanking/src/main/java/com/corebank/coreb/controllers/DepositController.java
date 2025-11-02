package com.corebank.coreb.controllers;

import com.corebank.coreb.entity.Deposit;
import com.corebank.coreb.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deposits")
public class DepositController {

    @Autowired
    private DepositService depositService;

    // Create a new deposit
    @PostMapping
    public ResponseEntity<Deposit> createDeposit(@RequestBody Deposit deposit) {
        Deposit savedDeposit = depositService.save(deposit);
        return ResponseEntity.ok(savedDeposit);
    }

    // Get all deposits
    @GetMapping
    public ResponseEntity<List<Deposit>> getAllDeposits() {
        List<Deposit> deposits = depositService.getAll();
        return ResponseEntity.ok(deposits);
    }

    // Get deposit by ID
    @GetMapping("/{id}")
    public ResponseEntity<Deposit> getDepositById(@PathVariable Long id) {
        return depositService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------- Update / Delete -----------------

    // Update deposit (e.g., if user adds more principal or changes status)
    @PutMapping("/{id}")
    public ResponseEntity<Deposit> updateDeposit(@PathVariable Long id, @RequestBody Deposit deposit) {
        return depositService.getById(id)
                .map(existingDeposit -> {
                    existingDeposit.setPrincipalAmount(deposit.getPrincipalAmount());
                    existingDeposit.setInterestRate(deposit.getInterestRate());
                    existingDeposit.setDepositType(deposit.getDepositType());
                    existingDeposit.setStartDate(deposit.getStartDate());
                    existingDeposit.setMaturityDate(deposit.getMaturityDate());
                    existingDeposit.setStatus(deposit.getStatus());
                    Deposit updatedDeposit = depositService.save(existingDeposit);
                    return ResponseEntity.ok(updatedDeposit);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Delete deposit
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDeposit(@PathVariable Long id) {
        boolean deleted = depositService.delete(id);
        if(deleted) {
            return ResponseEntity.ok("Deposit deleted successfully");
        } else {
            return ResponseEntity.status(400).body("Deposit not found or cannot be deleted");
        }
    }
}
