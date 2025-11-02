package com.corebank.coreb.entity;

// add imports
import com.corebank.coreb.enums.RoleType;
import com.corebank.coreb.enums.UserStatus;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "staff_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StaffUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String passwordHash;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private LocalDateTime lastLogin;

    // NEW: lockout & tracking fields
    private Integer failedLoginAttempts = 0;
    private Boolean accountLocked = false;     // true if admin locked or auto-locked
    private LocalDateTime lockTimestamp;       // when account was locked
    private LocalDateTime passwordExpiry;      // optional password expiry
    private LocalDateTime lastActivityAt;      // last API request time (optional)

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @OneToMany(mappedBy = "staffUser")
    private List<Log> logs;
}
