package com.corebank.coreb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchDTO {
    private Long branchId;
    private String name;
    private String address;
    private String phoneNumber;
    private String email;
}
