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
@Table(name = "billing")
public class Billing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long billingId; // PK

    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan; // FK → Loan

    @ManyToOne
    @JoinColumn(name = "repayment_id", nullable = false)
    private Repayment repayment; // FK → Repayment

    private LocalDate billingDate; // Billing generation date

    private LocalDate dueDate; // EMI due date

    private BigDecimal amountDue; // Expected amount

    private BigDecimal amountPaid; // Actual amount paid

    private String status; // Paid / Pending / Overdue

    private String paymentReference; // Optional payment reference

    private Boolean billingDone; // True if billing generated

    private String remarks; // Optional notes

    // Getters and Setters
}
