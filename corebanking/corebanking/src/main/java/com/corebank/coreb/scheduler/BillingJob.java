package com.corebank.coreb.scheduler;

import com.corebank.coreb.entity.Job;
import com.corebank.coreb.repository.JobRepository;
import com.corebank.coreb.service.BillingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class BillingJob {

    @Autowired
    private BillingService billingService;

    @Autowired
    private JobRepository jobRepository;

    // Runs daily at 1 AM
    @Scheduled(cron = "0 0 1 * * ?")
    public void processBilling() {
        LocalDate today = LocalDate.now();
        Job jobLog = new Job();
        jobLog.setJobType("BillingJob");
        jobLog.setStartTime(LocalDateTime.now());
        jobLog.setExecutionMode("Automatic");
        jobLog.setProcessedDate(today);

        try {
            int processedCount = billingService.generateBilling(today);
            jobLog.setStatus("Success");
            jobLog.setRemarks("Processed " + processedCount + " repayments for billing.");
        } catch (Exception e) {
            jobLog.setStatus("Failed");
            jobLog.setRemarks("Error during billing job: " + e.getMessage());
        } finally {
            jobLog.setEndTime(LocalDateTime.now());
            jobRepository.save(jobLog);
        }
    }
}
