package com.corebank.coreb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId; // Unique identifier

    private String firstName;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email; // Login / contact

    private String phone;

    private LocalDate dob; // Date of Birth

    @Column(columnDefinition = "TEXT")
    private String addressLine1;

    @Column(columnDefinition = "TEXT")
    private String city;

    @Column(columnDefinition = "TEXT")
    private String state;

    @Column(length = 20)
    private String zip;

    @Column(columnDefinition = "TEXT")
    private String kycDetails; // ID documents / verification info

    private LocalDateTime createdAt;

    private String status; // Active / Inactive

    // Relationships
    @OneToMany(mappedBy = "customer")
    private List<Account> accounts;

    @OneToMany(mappedBy = "customer")
    private List<Loan> loans;

    @ManyToOne
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch; // FK → Branch

    // Automatically set createdAt on persist
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "Active";
        }
    }
}
