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
@Table(name = "card")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cardId; // PK

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account; // FK → Account

    @Column(unique = true)
    private String cardNumber;

    private String cardType; // Debit / Credit

    private LocalDate expiryDate;

    private String status; // active / inactive

    // ----- Credit card specific fields -----
    @Column(precision = 15, scale = 2)
    private BigDecimal creditLimit;       // Nullable for debit cards

    @Column(precision = 15, scale = 2)
    private BigDecimal outstandingBalance; // Nullable for debit cards

    private LocalDate dueDate;            // Nullable for debit cards

    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate;      // Nullable for debit cards

    @Column(precision = 15, scale = 2)
    private BigDecimal minimumPayment;    // Nullable for debit cards
}
