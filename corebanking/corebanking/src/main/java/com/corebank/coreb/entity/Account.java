package com.corebank.coreb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private String accountType;

    @Column(precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO; // initialize to 0

    private String status;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

//    @OneToOne
//    @JoinColumn(name = "cheque_id")
//    private ChequeBook chequeBook;

    @OneToOne
    @JoinColumn(name = "card_id")
    private Card card;
}
