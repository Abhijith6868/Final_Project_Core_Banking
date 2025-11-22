package com.corebank.coreb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JobMasterDTO {

    private Long jobid;
    private String jobName;
    private String description;
    private String cronExpression;
    private Boolean active;
    private String apiEndpoint;

    // Computed (NOT stored in DB)
    private String lastStatus;
    private String lastRunTime;
}
