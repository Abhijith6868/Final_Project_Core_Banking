package com.corebank.coreb.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobResponseDTO {

    private Long seqNo;             // Execution ID
    private Long jobid;             // JobMaster ID (FK)

    private String jobName;         // from JobMaster
    private String status;          // COMPLETED / FAILED / RUNNING
    private String executionMode;   // MANUAL / AUTOMATIC

    private LocalDate processedDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String remarks;

    private BillingResponseDTO billingResponse;
}
