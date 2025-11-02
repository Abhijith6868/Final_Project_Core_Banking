package com.corebank.coreb.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "branch")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long branchId;  // PK

    private String name;

    private String address;

    private String phoneNumber;

    private String email;

    // No need to reference staffUsers, accounts, or loans here
}
