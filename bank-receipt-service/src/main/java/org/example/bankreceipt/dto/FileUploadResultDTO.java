package org.example.bankreceipt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileUploadResultDTO {
    private String fileName;
    private String fileSize;
    private String storagePath;
}