package com.corebank.coreb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "deposit")
public class Deposit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long depositId; // PK

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // FK → Customer

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account; // FK → Account

    private String depositType; // DEPOSIT / WITHDRAWAL / INTEREST

    @Column(precision = 15, scale = 2)
    private BigDecimal principalAmount; // Amount deposited or withdrawn

    private BigDecimal interestRate; // Rate of interest (only used for interest deposits)

    private LocalDate startDate; // Transaction date

    private LocalDate maturityDate; // Optional: can be null for normal deposits

    private String status; // ACTIVE / INACTIVE / WITHDRAWN
}
