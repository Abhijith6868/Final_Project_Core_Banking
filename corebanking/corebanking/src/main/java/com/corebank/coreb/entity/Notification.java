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
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId; // PK

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // FK → Customer

    private String type; // EMI Due / Payment Received / Account Alert / Other

    private String message;

    private String sentStatus; // Pending / Sent / Failed

    private LocalDateTime createdAt;

    private LocalDateTime sentAt; // nullable

    // Getters and Setters
}
