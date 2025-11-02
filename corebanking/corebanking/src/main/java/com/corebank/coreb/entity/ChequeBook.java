//package com.corebank.coreb.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.time.LocalDate;
//
//@Data
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "cheque_book")
//public class ChequeBook {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long chequeId; // PK
//
//    @ManyToOne
//    @JoinColumn(name = "account_id", nullable = false)
//    private Account account; // FK → Account
//
//    private LocalDate issuedDate;
//
//    private Integer numberOfLeaves;
//
//    private String status; // active / used
//
//    // Getters and Setters
//}
