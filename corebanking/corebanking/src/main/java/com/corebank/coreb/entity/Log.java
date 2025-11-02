package com.corebank.coreb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "log")
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId; // PK

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private StaffUser staffUser; // FK → StaffUser

    private String actionType; // Create / Update / Delete / Login / Transaction / Other

    private String entityName; // Table or module affected

    private Long entityId; // ID of the affected record

    private LocalDateTime actionTimestamp;

    private String description; // Optional details

    // Getters and Setters
}
