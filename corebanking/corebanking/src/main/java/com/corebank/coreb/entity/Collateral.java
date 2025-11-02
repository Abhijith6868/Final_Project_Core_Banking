package com.corebank.coreb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "collateral")
public class Collateral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long collateralId; // PK

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan; // FK → Loan

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // FK → Customer

    private String collateralType; // Property / Vehicle / Gold / Other

    @Column(columnDefinition = "TEXT")
    private String description; // Description of the collateral

    @Column(precision = 15, scale = 2)
    private BigDecimal estimatedValue; // Estimated value

    private LocalDate pledgedDate; // Date collateral was pledged

    private String status; // Active / Released / Seized

    // Getters and Setters
}
