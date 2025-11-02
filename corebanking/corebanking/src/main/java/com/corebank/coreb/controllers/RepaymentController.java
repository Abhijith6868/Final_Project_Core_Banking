package com.corebank.coreb.controllers;

import com.corebank.coreb.entity.Repayment;
import com.corebank.coreb.service.RepaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/repayments")
@CrossOrigin(origins = "*") // allow frontend access
public class RepaymentController {

    @Autowired
    private RepaymentService repaymentService;

    // --------------------
    // Create a new repayment manually
    // --------------------
    @PostMapping
    public ResponseEntity<Repayment> createRepayment(@RequestBody Repayment repayment) {
        Repayment saved = repaymentService.saveRepayment(repayment);
        return ResponseEntity.ok(saved);
    }

    // --------------------
    // Update a repayment manually
    // --------------------
    @PutMapping("/{repaymentId}")
    public ResponseEntity<Repayment> updateRepayment(@PathVariable Long repaymentId,
                                                     @RequestBody Repayment repayment) {
        repayment.setRepaymentId(repaymentId);
        Repayment updated = repaymentService.updateRepayment(repayment);
        return ResponseEntity.ok(updated);
    }

    // --------------------
    // Process Payment (Partial / Full)
    // --------------------
    @PutMapping("/{repaymentId}/pay")
    public ResponseEntity<Repayment> processPayment(@PathVariable Long repaymentId,
                                                    @RequestParam BigDecimal amountPaid) {
        Repayment updated = repaymentService.processPayment(repaymentId, amountPaid);
        return ResponseEntity.ok(updated);
    }

    // --------------------
    // Get repayment by ID
    // --------------------
    @GetMapping("/{repaymentId}")
    public ResponseEntity<Repayment> getRepaymentById(@PathVariable Long repaymentId) {
        Optional<Repayment> repayment = repaymentService.getRepaymentById(repaymentId);
        return repayment.map(ResponseEntity::ok)
                        .orElse(ResponseEntity.notFound().build());
    }

    // --------------------
    // Get all repayments
    // --------------------
    @GetMapping
    public ResponseEntity<List<Repayment>> getAllRepayments() {
        List<Repayment> repayments = repaymentService.getAllRepayments();
        return ResponseEntity.ok(repayments);
    }

    // --------------------
    // Get repayments by loan ID
    // --------------------
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<Repayment>> getByLoan(@PathVariable Long loanId) {
        List<Repayment> repayments = repaymentService.getRepaymentsByLoan(loanId);
        return ResponseEntity.ok(repayments);
    }

    // --------------------
    // Get repayments by customer ID
    // --------------------
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Repayment>> getByCustomer(@PathVariable Long customerId) {
        List<Repayment> repayments = repaymentService.getRepaymentsByCustomer(customerId);
        return ResponseEntity.ok(repayments);
    }

    // --------------------
    // Delete repayment (if not paid)
    // --------------------
    @DeleteMapping("/{repaymentId}")
    public ResponseEntity<String> deleteRepayment(@PathVariable Long repaymentId) {
        boolean success = repaymentService.deleteRepayment(repaymentId);
        if (success) {
            return ResponseEntity.ok("Repayment deleted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Cannot delete a paid repayment.");
        }
    }
}
