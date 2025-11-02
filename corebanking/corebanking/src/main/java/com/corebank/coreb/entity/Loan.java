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
@Table(name = "loan")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanId; // Unique loan identifier

    @Column(nullable = false, unique = true)
    private String loanNo; // Human-readable loan number

    // Relationships
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // FK → Customer

    @ManyToOne
    @JoinColumn(name = "collateral_id")
    private Collateral collateral; // Nullable → Unsecured loan if NULL

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch; // FK → Branch

    private String loanType; // Home / Personal / Auto etc.

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal principal; // Original principal amount
    
    @Column(precision = 15, scale = 2)
    private BigDecimal balancePrincipal;

    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate; // Annual interest rate (%)

    private Integer tenureMonths; // Loan duration in months

    private LocalDate startDate; // Loan start date
    
    @Column(nullable = false)
    private LocalDate maturityDate; // Loan end date / last EMI date

    private String status; // Active / Closed / Default

    // Getters and Setters
    
 // Automatically generate loan number before saving
    @PrePersist
    public void prePersist() {
        if (this.loanNo == null || this.loanNo.isEmpty()) {
            this.loanNo = generateLoanNo();
        }
    }

    private String generateLoanNo() {
        // Example format: LN-YYYYMMDD-RANDOM
        String datePart = java.time.LocalDate.now()
                             .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = (int) (Math.random() * 9000) + 1000; // random 4 digits
        return "LN" + datePart + random;
    }
}
