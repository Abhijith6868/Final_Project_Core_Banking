package com.corebank.coreb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RepaymentReportDTO {

    private Long loanId;
    private LocalDate dueDate;
    private LocalDate paymentDate;

    private BigDecimal amountPaid;
    private BigDecimal rateofinterest;
    private BigDecimal balanceRemaining;
    private BigDecimal outstanding;
}
