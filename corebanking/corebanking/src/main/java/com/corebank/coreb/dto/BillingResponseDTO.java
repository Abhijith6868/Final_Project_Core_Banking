package com.corebank.coreb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillingResponseDTO {
    private LocalDate billingDate;
    private int processedCount;
    private String remarks;
    private List<BillingDTO> billedRecords;
}
