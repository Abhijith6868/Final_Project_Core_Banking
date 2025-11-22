package com.corebank.coreb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingDTO {
    private Long billingId;
    private Long loanId;
    private Long repaymentId;
    private LocalDate billingDate;
    private BigDecimal amountDue;
    private BigDecimal amountPaid;
    private String status;
    private String remarks;
}
