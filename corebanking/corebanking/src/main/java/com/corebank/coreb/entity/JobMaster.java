package com.corebank.coreb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job_master")
public class JobMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobid;

    private String jobName;

    @Column(length = 500)
    private String description;

    private String cronExpression;

    private Boolean active = true;

    @Column(name = "api_endpoint")
    private String apiEndpoint;
}
