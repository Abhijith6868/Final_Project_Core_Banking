package com.corebank.coreb.controllers;

import com.corebank.coreb.entity.Job;
import com.corebank.coreb.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    // Create a new job
    @PostMapping
    public Job createJob(@RequestBody Job job) {
        return jobService.saveJob(job);
    }

    // Read all jobs
    @GetMapping
    public List<Job> getAllJobs() {
        return jobService.getAllJobs();
    }

    // Read job by ID
    @GetMapping("/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update a job
    @PutMapping("/{id}")
    public ResponseEntity<Job> updateJob(@PathVariable Long id, @RequestBody Job jobDetails) {
        return jobService.getJobById(id)
                .map(job -> {
                    job.setJobType(jobDetails.getJobType());
                    job.setStartTime(jobDetails.getStartTime());
                    job.setEndTime(jobDetails.getEndTime());
                    job.setExecutionMode(jobDetails.getExecutionMode());
                    job.setStatus(jobDetails.getStatus());
                    job.setRemarks(jobDetails.getRemarks());
                    job.setUserId(jobDetails.getUserId());
                    job.setProcessedDate(jobDetails.getProcessedDate());

                    Job updatedJob = jobService.saveJob(job);
                    return ResponseEntity.ok(updatedJob);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a job
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(job -> {
                    jobService.deleteJob(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
