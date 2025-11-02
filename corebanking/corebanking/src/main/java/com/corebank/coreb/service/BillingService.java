package com.corebank.coreb.service;

import com.corebank.coreb.entity.Billing;
import com.corebank.coreb.entity.Repayment;
import com.corebank.coreb.repository.BillingRepository;
import com.corebank.coreb.repository.RepaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class BillingService {

    @Autowired
    private RepaymentRepository repaymentRepository;

    @Autowired
    private BillingRepository billingRepository;

    /**
     * Generate billing for all due repayments that have not been billed yet.
     */
    public int generateBilling(LocalDate billingDate) {
        List<Repayment> repayments = repaymentRepository
                .findByBillingDoneFalseAndDueDateLessThanEqual(billingDate);

        int processedCount = 0;

        for (Repayment repayment : repayments) {

            BigDecimal expectedPrincipal = repayment.getExpectedPrincipal() != null 
                    ? repayment.getExpectedPrincipal() 
                    : BigDecimal.ZERO;
            BigDecimal expectedInterest = repayment.getExpectedInterest() != null 
                    ? repayment.getExpectedInterest() 
                    : BigDecimal.ZERO;
            BigDecimal totalDue = expectedPrincipal.add(expectedInterest);

            BigDecimal amountPaid = repayment.getAmountPaid() != null ? repayment.getAmountPaid() : BigDecimal.ZERO;

            // Calculate principal paid in this installment
            BigDecimal principalPaid = repayment.getPrincipalPaid() != null ? repayment.getPrincipalPaid() : BigDecimal.ZERO;

            // Update remaining principal and outstanding interest
            BigDecimal newRemainingPrincipal = repayment.getRemainingPrincipal()
                    .subtract(principalPaid)
                    .max(BigDecimal.ZERO);

            BigDecimal newOutstandingInterest = expectedInterest.subtract(repayment.getInterestPaid() != null ? repayment.getInterestPaid() : BigDecimal.ZERO)
                    .max(BigDecimal.ZERO);

            repayment.setRemainingPrincipal(newRemainingPrincipal);
            repayment.setOutstandingInterest(newOutstandingInterest);

            // Update repayment status
            if (amountPaid.compareTo(totalDue) >= 0) {
                repayment.setStatus("PAID");
            } else if (billingDate.isAfter(repayment.getDueDate())) {
                repayment.setStatus("OVERDUE");
            } else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
                repayment.setStatus("PARTIAL");
            } else {
                repayment.setStatus("UNPAID");
            }

            // Generate billing record
            Billing billing = new Billing();
            billing.setLoan(repayment.getLoan());
            billing.setRepayment(repayment);
            billing.setBillingDate(billingDate);
            billing.setDueDate(repayment.getDueDate());
            billing.setAmountDue(totalDue);
            billing.setAmountPaid(amountPaid);
            billing.setStatus(repayment.getStatus());
            billing.setBillingDone(true);
            billing.setRemarks("Auto-generated billing");

            billingRepository.save(billing);

            // Mark repayment as billed
            repayment.setBillingDone(true);
            repaymentRepository.save(repayment);

            processedCount++;
        }

        return processedCount;
    }
}
