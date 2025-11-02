package com.corebank.coreb.dto;

import com.corebank.coreb.enums.RoleType;
import com.corebank.coreb.enums.UserStatus;
import lombok.Data;

@Data
public class StaffUserDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private RoleType role;
    private UserStatus status;
    private String password; 
    private Boolean accountLocked;
    private Integer failedLoginAttempts;
    private Long branchId;   // ✅ renamed for clarity
    private String createdBy;
    private String updatedBy;
}
