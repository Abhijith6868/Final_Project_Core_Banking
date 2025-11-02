package com.corebank.coreb.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "repayment")
public class Repayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long repaymentId; // Auto-generated PK

    // === Relationships ===
    @ManyToOne
    @JoinColumn(name = "loan_id", nullable = false)
    private Loan loan; // FK → Loan.loan_id

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer; // FK → Customer.customer_id


    // === Payment Schedule ===
    private LocalDate dueDate; // Scheduled EMI due date
    private LocalDate paymentDate; // Actual payment date (if paid)

    // === Expected Values (System Calculated) ===
    @Column(precision = 15, scale = 2)
    private BigDecimal expectedPrincipal; // Expected principal for this period

    @Column(precision = 15, scale = 2)
    private BigDecimal expectedInterest; // Interest due for this period (dynamic)

    @Column(precision = 15, scale = 2)
    private BigDecimal totalDue; // expectedPrincipal + expectedInterest


    // === Actual Values (User Paid) ===
    @Column(precision = 15, scale = 2)
    private BigDecimal amountPaid; // Actual amount paid

    @Column(precision = 15, scale = 2)
    private BigDecimal principalPaid; // Actual principal component paid

    @Column(precision = 15, scale = 2)
    private BigDecimal interestPaid; // Actual interest component paid


    // === Balances ===
    @Column(precision = 15, scale = 2)
    private BigDecimal remainingPrincipal; // After this repayment

    @Column(precision = 15, scale = 2)
    private BigDecimal outstandingInterest; // If customer underpays interest


    // === Rate and Status ===
    @Column(precision = 5, scale = 2)
    private BigDecimal rateOfInterest; // Interest rate used for this period

    private String status; // PAID / PARTIAL / OVERDUE / UNPAID

    private String receiptNumber; // Auto-generated voucher or transaction number

    private Boolean billingDone; // Whether this installment has been invoiced
}
