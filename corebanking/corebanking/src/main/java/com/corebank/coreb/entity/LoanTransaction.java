package com.corebank.coreb.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan_transaction")
public class LoanTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanTransactionId; // PK

    // Relationships
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan; // FK → Loan.loan_id

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // FK → Customer.customer_id

    private LocalDateTime transactionDate; // Date and time of the loan transaction

    @Column(precision = 15, scale = 2)
    private BigDecimal amount; // Transaction amount

    private String transactionType; // Disbursement / EMI Payment / Interest / Penalty / Other

    private String status; // Completed / Pending / Failed

    private String remarks; // Optional description

    // Getters and Setters
}
