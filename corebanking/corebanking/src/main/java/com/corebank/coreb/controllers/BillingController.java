package com.corebank.coreb.controllers;

import com.corebank.coreb.dto.BillingDTO;
import com.corebank.coreb.dto.BillingResponseDTO;
import com.corebank.coreb.entity.Billing;
import com.corebank.coreb.repository.BillingRepository;
import com.corebank.coreb.service.BillingService;
import com.corebank.coreb.service.SystemDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private SystemDateService systemDateService;

    /**
     * ✅ Manually trigger billing based on the system date (for Manager/Admin)
     */
    @PostMapping("/generate")
    public ResponseEntity<BillingResponseDTO> generateBillingNow() {
        BillingResponseDTO response = billingService.generateBilling();
        return ResponseEntity.ok(response);
    }

    /**
     * ✅ Get current system billing date
     */
    @GetMapping("/date")
    public ResponseEntity<String> getCurrentBillingDate() {
        return ResponseEntity.ok("Current System Billing Date: " + systemDateService.getSystemDate());
    }

    /**
     * ✅ Get all billing records (mapped to DTO)
     */
    @GetMapping("/all")
    public ResponseEntity<List<BillingDTO>> getAllBillings() {
        List<BillingDTO> billings = billingRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(billings);
    }

    /**
     * ✅ Get billing records for a specific loan
     */
    @GetMapping("/loan/{loanId}")
    public ResponseEntity<List<BillingDTO>> getBillingsByLoan(@PathVariable Long loanId) {
        List<BillingDTO> billings = billingRepository.findByLoan_LoanId(loanId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(billings);
    }

    /**
     * ✅ Get billing records for a specific repayment
     */
    @GetMapping("/repayment/{repaymentId}")
    public ResponseEntity<List<BillingDTO>> getBillingByRepayment(@PathVariable Long repaymentId) {
        List<BillingDTO> billings = billingRepository.findByRepayment_RepaymentId(repaymentId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(billings);
    }

    /**
     * ✅ Mapper: Converts Billing entity → BillingDTO
     */
    private BillingDTO mapToDTO(Billing billing) {
        return new BillingDTO(
                billing.getBillingId(),
                billing.getLoan() != null ? billing.getLoan().getLoanId() : null,
                billing.getRepayment() != null ? billing.getRepayment().getRepaymentId() : null,
                billing.getBillingDate(),
                billing.getAmountDue(),
                billing.getAmountPaid(),
                billing.getStatus(),
                billing.getRemarks()
        );
    }
}
