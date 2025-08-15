package org.example.bankreceipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DetailDTO {
    private String detailNo;
    private String status;
    private String transactionDate;
    private String amount;
    private int matchedCount;
}