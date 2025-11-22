package com.corebank.coreb.controllers;

import com.corebank.coreb.dto.JobMasterDTO;
import com.corebank.coreb.dto.JobResponseDTO;
import com.corebank.coreb.entity.Job;
import com.corebank.coreb.service.JobService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private JobService jobService;

    /**
     * 🟩 Get ALL job definitions (JobMaster)
     */
    @GetMapping("/master")
    public ResponseEntity<List<JobMasterDTO>> getAllJobDefinitions() {
        return ResponseEntity.ok(jobService.getAllJobMaster());
    }

    /**
     * 🟩 Run a Job using JobMaster jobid
     */
    @PostMapping("/run/{jobid}")
    public ResponseEntity<JobResponseDTO> runJob(@PathVariable Long jobid) {
        return ResponseEntity.ok(jobService.runJob(jobid));
    }

    /**
     * 🟩 Get job execution history for a specific jobid
     */
    @GetMapping("/history/{jobid}")
    public ResponseEntity<List<Job>> getJobHistory(@PathVariable Long jobid) {
        return ResponseEntity.ok(jobService.getJobHistory(jobid));
    }
}
