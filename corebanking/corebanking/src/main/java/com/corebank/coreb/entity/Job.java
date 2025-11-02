package com.corebank.coreb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "job")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    private String jobType;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private String executionMode; // Manual / Automatic
    private String status;        // Success / Failed / Partial

    @Column(columnDefinition = "TEXT")
    private String remarks;

    private Long userId;          // Nullable if automatic
    private LocalDate processedDate;

    // Getters and Setters
}
