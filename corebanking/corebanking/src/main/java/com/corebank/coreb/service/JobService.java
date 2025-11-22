package com.corebank.coreb.service;

import com.corebank.coreb.dto.JobMasterDTO;
import com.corebank.coreb.dto.JobResponseDTO;
import com.corebank.coreb.dto.BillingResponseDTO;
import com.corebank.coreb.entity.Job;
import com.corebank.coreb.entity.JobMaster;
import com.corebank.coreb.repository.JobMasterRepository;
import com.corebank.coreb.repository.JobRepository;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JobService {

    @Autowired
    private JobMasterRepository jobMasterRepository;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private BillingService billingService;

    @Autowired
    private SystemDateService systemDateService;


    // ============================================================
    //  1️⃣  GET ALL JOB MASTER (STATIC JOB LIST + LAST RUN INFO)
    // ============================================================
    public List<JobMasterDTO> getAllJobMaster() {
        List<JobMaster> list = jobMasterRepository.findAll();

        return list.stream().map(this::buildJobMasterDTO).collect(Collectors.toList());
    }


    // Build JobMasterDTO with last run info
    private JobMasterDTO buildJobMasterDTO(JobMaster master) {

        Job lastExecution = jobRepository.findTopByJobMasterOrderBySeqNoDesc(master);

        String lastStatus = (lastExecution != null) ? lastExecution.getStatus() : "NEVER_RUN";
        String lastRunTime = (lastExecution != null && lastExecution.getStartTime() != null)
                ? lastExecution.getStartTime().toString()
                : "-";

        return new JobMasterDTO(
                master.getJobid(),
                master.getJobName(),
                master.getDescription(),
                master.getCronExpression(),
                master.getActive(),
                master.getApiEndpoint(),
                lastStatus,
                lastRunTime
        );
    }


    // ============================================================
    //  2️⃣  RUN JOB USING JOB MASTER ID
    // ============================================================
    public JobResponseDTO runJob(Long jobid) {

        // Load job definition
        JobMaster jobMaster = jobMasterRepository.findById(jobid)
                .orElseThrow(() -> new RuntimeException("❌ JobMaster not found for ID: " + jobid));

        // Create execution log in Job table
        Job job = new Job();
        job.setJobMaster(jobMaster);
        job.setJobType(jobMaster.getJobName());
        job.setExecutionMode("MANUAL");
        job.setStatus("RUNNING");

        LocalDate systemDate = systemDateService.getSystemDate();
        LocalDateTime startTime = LocalDateTime.now();

        job.setStartTime(startTime);
        job.setProcessedDate(systemDate);

        jobRepository.save(job);  // 🔥 save initial execution log

        log.info("🚀 Starting Job [{}] | Name: {} | System Date: {}",
                job.getSeqNo(), jobMaster.getJobName(), systemDate);

        BillingResponseDTO billingResult = null;

        try {
            switch (jobMaster.getJobName().toUpperCase()) {

                case "LOAN_BILLING":
                    billingResult = billingService.generateBilling(job);
                    job.setStatus("COMPLETED");
                    job.setRemarks("Loan billing completed successfully.");
                    break;

                default:
                    job.setStatus("SKIPPED");
                    job.setRemarks("⚠️ No job handler defined for: " + jobMaster.getJobName());
                    break;
            }
        }
        catch (Exception e) {
            job.setStatus("FAILED");
            job.setRemarks("Job failed: " + e.getMessage());
            log.error("❌ Error executing job [{}]: {}", job.getSeqNo(), e.getMessage());
        }
        finally {
            job.setEndTime(LocalDateTime.now());
            jobRepository.save(job);

            log.info("🏁 Job [{}] finished with status: {} at {}",
                    job.getSeqNo(), job.getStatus(), job.getEndTime());
        }

        // Return detailed DTO to frontend
        return new JobResponseDTO(
                job.getSeqNo(),
                jobMaster.getJobid(),     // jobid from master
                jobMaster.getJobName(),
                job.getStatus(),
                job.getExecutionMode(),
                job.getProcessedDate(),
                job.getStartTime(),
                job.getEndTime(),
                job.getRemarks(),
                billingResult
        );
    }


    // ============================================================
    //  3️⃣  GET JOB EXECUTION HISTORY (JOB TABLE)
    // ============================================================
    public List<Job> getJobHistory(Long jobid) {

        JobMaster jobMaster = jobMasterRepository.findById(jobid)
                .orElseThrow(() -> new RuntimeException("JobMaster not found"));

        return jobRepository.findByJobMasterOrderBySeqNoDesc(jobMaster);
    }
}
