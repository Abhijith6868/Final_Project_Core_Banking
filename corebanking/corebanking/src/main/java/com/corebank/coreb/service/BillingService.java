package com.corebank.coreb.service;

import com.corebank.coreb.dto.BillingResponseDTO;
import com.corebank.coreb.dto.BillingDTO;
import com.corebank.coreb.entity.Billing;
import com.corebank.coreb.entity.Job;
import com.corebank.coreb.entity.Repayment;
import com.corebank.coreb.repository.BillingRepository;
import com.corebank.coreb.repository.JobRepository;
import com.corebank.coreb.repository.RepaymentRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
public class BillingService {

    @Autowired
    private RepaymentRepository repaymentRepository;

    @Autowired
    private BillingRepository billingRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private SystemDateService systemDateService;

    /**
     * 🔹 Run billing without job (manual API call)
     */
    public BillingResponseDTO generateBilling() {
        return generateBilling(null);
    }

    /**
     * 🔹 Run billing linked to job execution log
     */
    public BillingResponseDTO generateBilling(Job job) {

        LocalDate billingDate = systemDateService.getSystemDate();
        log.info("🔄 Starting Loan Billing | System Date: {}", billingDate);

        boolean isJobMode = (job != null);
        LocalDateTime startTime = LocalDateTime.now();
        String remarks;

        /*
         * 🔹 Initialize job execution metadata
         */
        if (isJobMode) {
            job.setStartTime(startTime);
            job.setStatus("RUNNING");

            // Set job type from JobMaster
            if (job.getJobMaster() != null) {
                job.setJobType(job.getJobMaster().getJobName());
            } else {
                job.setJobType("LOAN_BILLING");
            }

            job.setExecutionMode(
                    job.getExecutionMode() != null ? job.getExecutionMode() : "MANUAL"
            );

            job.setProcessedDate(billingDate);
            jobRepository.save(job);
        }

        List<BillingDTO> billingRecords = new ArrayList<>();
        int processedCount = 0;

        try {
            /*
             * 🔹 Fetch repayments that need billing
             */
            List<Repayment> repayments = repaymentRepository
                    .findByBillingDoneFalseAndDueDateLessThanEqual(billingDate);

            if (repayments.isEmpty()) {
                remarks = "ℹ️ No repayments found for billing date: " + billingDate;
                log.info(remarks);

                if (isJobMode) {
                    job.setStatus("COMPLETED");
                    job.setRemarks(remarks);
                    job.setEndTime(LocalDateTime.now());
                    jobRepository.save(job);
                }

                return new BillingResponseDTO(
                        billingDate, 0, remarks, billingRecords
                );
            }

            /*
             * 🔹 Process each repayment
             */
            for (Repayment repayment : repayments) {
                try {
                    BigDecimal expectedPrincipal = safe(repayment.getExpectedPrincipal());
                    BigDecimal expectedInterest = safe(repayment.getExpectedInterest());
                    BigDecimal amountPaid = safe(repayment.getAmountPaid());
                    BigDecimal principalPaid = safe(repayment.getPrincipalPaid());
                    BigDecimal interestPaid = safe(repayment.getInterestPaid());
                    BigDecimal remainingPrincipal = safe(repayment.getRemainingPrincipal());

                    BigDecimal totalDue = expectedPrincipal.add(expectedInterest);

                    BigDecimal newRemainingPrincipal =
                            remainingPrincipal.subtract(principalPaid).max(BigDecimal.ZERO);

                    BigDecimal newOutstandingInterest =
                            expectedInterest.subtract(interestPaid).max(BigDecimal.ZERO);

                    repayment.setRemainingPrincipal(newRemainingPrincipal);
                    repayment.setOutstandingInterest(newOutstandingInterest);

                    /*
                     * 🔹 Update repayment status
                     */
                    if (amountPaid.compareTo(totalDue) >= 0) {
                        repayment.setStatus("PAID");
                    } else if (billingDate.isAfter(repayment.getDueDate())) {
                        repayment.setStatus("OVERDUE");
                    } else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
                        repayment.setStatus("PARTIAL");
                    } else {
                        repayment.setStatus("UNPAID");
                    }

                    /*
                     * 🔹 Create Billing record
                     */
                    Billing billing = new Billing();
                    billing.setLoan(repayment.getLoan());
                    billing.setRepayment(repayment);
                    billing.setBillingDate(billingDate);
                    billing.setDueDate(repayment.getDueDate());
                    billing.setAmountDue(totalDue);
                    billing.setAmountPaid(amountPaid);
                    billing.setStatus(repayment.getStatus());
                    billing.setBillingDone(true);
                    billing.setRemarks("Auto-generated billing on system date: " + billingDate);

                    billingRepository.save(billing);

                    repayment.setBillingDone(true);
                    repaymentRepository.save(repayment);

                    /*
                     * 🔹 Add to DTO list
                     */
                    billingRecords.add(new BillingDTO(
                            billing.getBillingId(),
                            repayment.getLoan().getLoanId(),
                            repayment.getRepaymentId(),
                            billing.getBillingDate(),
                            billing.getAmountDue(),
                            billing.getAmountPaid(),
                            billing.getStatus(),
                            billing.getRemarks()
                    ));

                    processedCount++;

                } catch (Exception ex) {
                    log.error("❌ Error processing repayment ID {}: {}",
                            repayment.getRepaymentId(),
                            ex.getMessage()
                    );
                }
            }

            remarks = "✅ Billing completed successfully for " + processedCount +
                    " repayments (System Date: " + billingDate + ")";
            log.info(remarks);

            if (isJobMode) {
                job.setStatus("COMPLETED");
                job.setRemarks(remarks);
            }

        } catch (Exception e) {
            remarks = "❌ Billing process failed: " + e.getMessage();
            log.error(remarks);

            if (isJobMode) {
                job.setStatus("FAILED");
                job.setRemarks(remarks);
            }

        } finally {
            if (isJobMode) {
                job.setEndTime(LocalDateTime.now());
                jobRepository.save(job);

                log.info("🕒 Job execution [{}] finished at {}",
                        job.getSeqNo(),   // IMPORTANT CHANGE
                        job.getEndTime()
                );
            }
        }

        return new BillingResponseDTO(
                billingDate, processedCount, remarks, billingRecords
        );
    }

    /**
     * 🔹 Safe BigDecimal (prevents null pointer)
     */
    private BigDecimal safe(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
