package com.corebank.coreb.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollateralDTO {
    private Long collateralId;

    private Long loanId;
    private String loanNumber; // optional if you want to display loan info

    private Long customerId;
    private String customerName; // optional if you want to display customer info

    private String collateralType;
    private String description;
    private BigDecimal estimatedValue;
    private LocalDate pledgedDate;
    private String status;
}
