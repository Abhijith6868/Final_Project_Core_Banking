package com.corebank.coreb.controllers;

import com.corebank.coreb.dto.LoanDTO;
import com.corebank.coreb.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;

    // --------------------
    // Create Loan
    // --------------------
    @PostMapping
    public ResponseEntity<LoanDTO> createLoan(@RequestBody LoanDTO loanDTO) {
        LoanDTO createdLoan = loanService.saveLoan(loanDTO);
        return ResponseEntity.ok(createdLoan);
    }
    
    // --------------------
    // Approve Loan (Generates Repayment Schedule)
    // --------------------
    @PutMapping("/{loanId}/approve")
    public ResponseEntity<LoanDTO> approveLoan(@PathVariable Long loanId) {
        LoanDTO approvedLoan = loanService.approveLoan(loanId);
        return ResponseEntity.ok(approvedLoan);
    }

    // --------------------
    // Update Loan (Safe: does not touch balancePrincipal)
    // --------------------
    @PutMapping("/{loanId}")
    public ResponseEntity<LoanDTO> updateLoan(@PathVariable Long loanId, @RequestBody LoanDTO loanDTO) {
        LoanDTO updatedLoan = loanService.updateLoan(loanId, loanDTO);
        return ResponseEntity.ok(updatedLoan);
    }

    // --------------------
    // Get Loan by ID
    // --------------------
    @GetMapping("/{loanId}")
    public ResponseEntity<LoanDTO> getLoanById(@PathVariable Long loanId) {
        return loanService.getLoanById(loanId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // --------------------
    // Get All Loans
    // --------------------
    @GetMapping
    public ResponseEntity<List<LoanDTO>> getAllLoans() {
        return ResponseEntity.ok(loanService.getAllLoans());
    }

    // --------------------
    // Deactivate Loan
    // --------------------
    @PutMapping("/{loanId}/deactivate")
    public ResponseEntity<String> deactivateLoan(@PathVariable Long loanId) {
        boolean success = loanService.deactivateLoan(loanId);
        if (success) {
            return ResponseEntity.ok("Loan deactivated successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed to deactivate loan");
        }
    }

    // --------------------
    // Delete Loan (Safe delete if Inactive/Closed)
    // --------------------
    @DeleteMapping("/{loanId}")
    public ResponseEntity<String> deleteLoan(@PathVariable Long loanId) {
        boolean deleted = loanService.safeDeleteLoan(loanId);
        if (deleted) {
            return ResponseEntity.ok("Loan deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Loan cannot be deleted: active or in-progress repayments exist");
        }
    }
}
