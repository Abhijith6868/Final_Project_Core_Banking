package com.corebank.coreb.controllers;

import com.corebank.coreb.entity.Billing;
import com.corebank.coreb.service.BillingService;
import com.corebank.coreb.repository.BillingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    @Autowired
    private BillingService billingService;

    @Autowired
    private BillingRepository billingRepository;

    /**
     * Manually trigger billing for today.
     */
    @PostMapping("/generate")
    public String generateBillingNow() {
        int processedCount = billingService.generateBilling(LocalDate.now());
        return "Billing processed for " + processedCount + " repayments.";
    }

    /**
     * Get all billing records.
     */
    @GetMapping("/all")
    public List<Billing> getAllBillings() {
        return billingRepository.findAll();
    }

    /**
     * Get billing records for a specific loan.
     */
    @GetMapping("/loan/{loanId}")
    public List<Billing> getBillingsByLoan(@PathVariable Long loanId) {
        return billingRepository.findByLoan_LoanId(loanId);
    }

    /**
     * Get billing records for a specific repayment.
     */
    @GetMapping("/repayment/{repaymentId}")
    public List<Billing> getBillingByRepayment(@PathVariable Long repaymentId) {
        return billingRepository.findByRepayment_RepaymentId(repaymentId);
    }
}
