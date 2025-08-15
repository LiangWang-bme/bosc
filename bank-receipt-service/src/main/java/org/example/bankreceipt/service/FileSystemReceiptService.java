package org.example.bankreceipt.service;


import org.example.bankreceipt.dto.*;
import org.example.bankreceipt.model.BankReceipt;
import org.example.bankreceipt.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class FileSystemReceiptService {

    @Value("${receipt.directory.path:D:\\0上海银行\\huidan\\测试环境-回单文件\\民生银行305\\5032523}")
    private String receiptDirectoryPath;

//    @Value("${receipt.directory.path:D:\\0上海银行\\huidan\\测试环境-回单文件\\上海农商银行322\\50131009300311531_9500090}")
//    private String receiptDirectoryPath;

    @Autowired
    private ReceiptRepository ReceiptRepository; // 注入 Repository

    public List<ReceiptFileDTO> listLocalReceiptFiles() {
        File directory = new File(receiptDirectoryPath);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("指定的回单目录不存在: " + receiptDirectoryPath);
        }

        return Arrays.stream(Objects.requireNonNull(directory.listFiles()))
                .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".pdf"))
                .map(file -> {
                    String fileName = file.getName();
                    // 从数据库查询该文件对应的 BankReceipt 记录
                    Optional<BankReceipt> receiptOpt = ReceiptRepository.findByFileName(fileName);

                    String status = "PENDING"; // 默认状态
                    if (receiptOpt.isPresent()) {
                        BankReceipt receipt = receiptOpt.get();
                        status = receipt.getStatus(); // 从数据库获取真实状态
                    }

                    return new ReceiptFileDTO(
                            fileName,
                            "民生银行", // 可以根据实际业务动态设置，或者从数据库获取
//                            "上海农商银行",
                            status,     // 动态状态：PENDING / PARSED
                            formatFileSize(file.length()),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()))
                    );
                })
                .collect(Collectors.toList());
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + "B";
        else if (size < 1024 * 1024) return (size / 1024) + "KB";
        else return (size / (1024 * 1024)) + "MB";
    }

    public String getReceiptDirectoryPath() {
        return receiptDirectoryPath;
    }
}