package com.corebank.coreb.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private Long accountId;
    private String accountType;
    private BigDecimal balance;
    private String status;

    private Long customer;
    private String customerName;

    private Long branch;
    private String branchName;
}