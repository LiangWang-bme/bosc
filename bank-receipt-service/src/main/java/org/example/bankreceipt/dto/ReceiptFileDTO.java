package org.example.bankreceipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReceiptFileDTO {
    private String fileName;
    private String bankName;
    private String status;
    private String fileSize;
    private String uploadTime;
}