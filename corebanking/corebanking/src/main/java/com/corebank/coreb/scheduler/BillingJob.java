package com.corebank.coreb.scheduler;

import com.corebank.coreb.dto.BillingResponseDTO;
import com.corebank.coreb.entity.Job;
import com.corebank.coreb.entity.JobMaster;
import com.corebank.coreb.repository.JobMasterRepository;
import com.corebank.coreb.repository.JobRepository;
import com.corebank.coreb.service.BillingService;
import com.corebank.coreb.service.SystemDateService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class BillingJob {

    @Autowired
    private BillingService billingService;

    @Autowired
    private SystemDateService systemDateService;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobMasterRepository jobMasterRepository;


    /**
     * 🕒 Runs daily at 1 AM
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processBilling() {

        // 1️⃣ Load JobMaster definition for LOAN_BILLING
        JobMaster jobMaster = jobMasterRepository.findByJobName("LOAN_BILLING")
                .orElseThrow(() -> new RuntimeException("❌ JobMaster entry for LOAN_BILLING not found"));

        // 2️⃣ Create new execution log entry
        LocalDate systemDate = systemDateService.getSystemDate();
        LocalDateTime startTime = LocalDateTime.now();

        Job job = new Job();
        job.setJobMaster(jobMaster);
        job.setJobType(jobMaster.getJobName());
        job.setExecutionMode("AUTOMATIC");
        job.setProcessedDate(systemDate);
        job.setStartTime(startTime);
        job.setStatus("RUNNING");

        jobRepository.save(job);

        log.info("🔄 [Scheduler] Billing job started | System Date: {} | Execution ID: {}", 
                systemDate, job.getSeqNo());

        try {
            // 3️⃣ Run billing with job tracking
            BillingResponseDTO result = billingService.generateBilling(job);

            job.setStatus("COMPLETED");
            job.setRemarks(result.getRemarks());

            log.info("✅ [Scheduler] Billing job completed. Processed {} repayments.",
                     result.getProcessedCount());
        }
        catch (Exception e) {
            job.setStatus("FAILED");
            job.setRemarks("❌ Billing job failed: " + e.getMessage());
            log.error("❌ [Scheduler] Billing job failed: {}", e.getMessage(), e);
        }
        finally {
            job.setEndTime(LocalDateTime.now());
            jobRepository.save(job);
            log.info("🕒 [Scheduler] Billing job finished at {} | Status: {}",
                    job.getEndTime(), job.getStatus());
        }
    }
}
