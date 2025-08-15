package org.example.bankreceipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchResultDTO {
    private String detailNo;
    private String matchedReceipt;
    private String matchTime;
}