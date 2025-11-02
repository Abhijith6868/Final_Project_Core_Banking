package com.corebank.coreb.controllers;

import com.corebank.coreb.dto.CollateralDTO;
import com.corebank.coreb.entity.Customer;
import com.corebank.coreb.entity.Loan;
import com.corebank.coreb.service.CollateralService;
import com.corebank.coreb.repository.CustomerRepository;
import com.corebank.coreb.repository.LoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/collaterals")
@CrossOrigin(origins = "*")
public class CollateralController {

    @Autowired
    private CollateralService collateralService;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private CustomerRepository customerRepository;

    // --------------------
    // Create Collateral
    // --------------------
    @PostMapping
    public ResponseEntity<?> createCollateral(@RequestBody CollateralDTO dto) {
        try {
            Loan loan = loanRepository.findById(dto.getLoanId())
                    .orElseThrow(() -> new RuntimeException("Loan not found"));
            Customer customer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));

            CollateralDTO saved = collateralService.saveCollateral(dto);
            return ResponseEntity.status(201).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error creating collateral: " + e.getMessage());
        }
    }

    // --------------------
    // Get Collateral by ID
    // --------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getCollateralById(@PathVariable Long id) {
        try {
            return collateralService.getCollateralById(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching collateral: " + e.getMessage());
        }
    }

    // --------------------
    // Get All Collaterals
    // --------------------
    @GetMapping
    public ResponseEntity<?> getAllCollaterals() {
        try {
            List<CollateralDTO> collaterals = collateralService.getAllCollaterals();
            return ResponseEntity.ok(collaterals);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error fetching collaterals: " + e.getMessage());
        }
    }

    // --------------------
    // Update Collateral
    // --------------------
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCollateral(@PathVariable Long id, @RequestBody CollateralDTO dto) {
        try {
            CollateralDTO updated = collateralService.updateCollateral(id, dto);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating collateral: " + e.getMessage());
        }
    }

    // --------------------
    // Deactivate Collateral (Soft Delete)
    // --------------------
    @PostMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivateCollateral(@PathVariable Long id) {
        try {
            collateralService.deactivateCollateral(id);
            return ResponseEntity.ok("Collateral deactivated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error deactivating collateral: " + e.getMessage());
        }
    }

    // --------------------
    // Safe Delete Collateral (Hard Delete)
    // --------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCollateral(@PathVariable Long id) {
        try {
            collateralService.safeDeleteCollateral(id);
            return ResponseEntity.ok(Map.of(
                "message", "Collateral deleted successfully",
                "collateralId", id
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage(),
                "collateralId", id
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "message", "Error deleting collateral: " + e.getMessage(),
                "collateralId", id
            ));
        }
    }

}
