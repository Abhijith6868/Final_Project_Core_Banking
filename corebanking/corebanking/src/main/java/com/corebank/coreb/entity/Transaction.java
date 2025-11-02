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
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId; // PK

    @ManyToOne
    @JoinColumn(name = "from_account_id")
    private Account fromAccount; // Sender (nullable for deposits)

    @ManyToOne
    @JoinColumn(name = "to_account_id")
    private Account toAccount; // Receiver (nullable for withdrawals)

    private LocalDateTime transactionDate;

    @Column(precision = 15, scale = 2)
    private BigDecimal amount;

    private String transactionType; // Credit / Debit / Transfer / Deposit / Withdrawal

    private String status; // Completed / Pending / Failed

    private String remarks; // Optional description

    // Getters and Setters
}
