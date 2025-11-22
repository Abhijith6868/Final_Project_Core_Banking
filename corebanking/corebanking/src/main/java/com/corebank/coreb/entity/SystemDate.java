package com.corebank.coreb.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class SystemDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "system_current_date") // ✅ avoid reserved word
    private LocalDate currentDate;

    private LocalDate updatedAt;
    private String updatedBy;
}