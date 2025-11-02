package com.corebank.coreb.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomerDTO {
    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate dob;

    // Expanded address fields
    private String addressLine1;
    private String city;
    private String state;
    private String zip;

    private String kycDetails;
    private LocalDateTime createdAt;
    private String status;
    private Long branchId;
    private String branchName;
}
