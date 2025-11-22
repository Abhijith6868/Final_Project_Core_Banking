package com.corebank.coreb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seqNo; // execution log id
    
    private String jobType;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String executionMode; 
    private String status;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    private Long userId;
    private LocalDate processedDate;

    @ManyToOne
    @JoinColumn(name = "jobid") // same FK name as PK of JobMaster
    private JobMaster jobMaster;
}
