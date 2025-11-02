package com.corebank.coreb.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {
    private Long loanId;
    private String loanNo;
    private Long customerId;
    private String customerName;
    private Long branchId;
    private String branchName;
    private Long collateralId;
    private String loanType;
    private BigDecimal principal;
    private BigDecimal interestRate;
    private Integer tenureMonths;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private String status;
}
