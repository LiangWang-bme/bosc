package org.example.bankreceipt.service;

import org.example.bankreceipt.dto.*;
import org.example.bankreceipt.model.BankReceipt;
import org.example.bankreceipt.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileSystemReceiptService {
    // 使用Map存储不同银行的路径配置
    private final Map<String, String> bankReceiptPaths;

    @Autowired
    private ReceiptRepository receiptRepository;

    public FileSystemReceiptService() {
        // 初始化银行路径配置
        this.bankReceiptPaths = new HashMap<>();
        this.bankReceiptPaths.put("CMBC", "D:\\0上海银行\\huidan\\测试环境-回单文件\\民生银行305\\5032523");
        this.bankReceiptPaths.put("SRCB", "D:\\0上海银行\\huidan\\测试环境-回单文件\\上海农商银行322\\50131009300311531_9500090");
        this.bankReceiptPaths.put("CITIC", "D:\\0上海银行\\huidan\\测试环境-回单文件\\中信银行302");
    }

    /**
     * 获取指定银行的回单目录路径
     */
    public String getBankReceiptDirectoryPath(String bankCode) {
        String path = bankReceiptPaths.get(bankCode);
        if (path == null) {
            throw new IllegalArgumentException("未配置的银行代码: " + bankCode);
        }
        return path;
    }

    /**
     * 获取默认回单目录路径
     */
    public String getReceiptDirectoryPath() {
        // 默认返回第一个配置的银行路径
        return bankReceiptPaths.values().iterator().next();
    }

    /**
     * 获取指定银行的回单文件列表
     */
    public List<ReceiptFileDTO> listBankReceiptFiles(String bankCode) {
        File directory = new File(getBankReceiptDirectoryPath(bankCode));

        if (!directory.exists() || !directory.isDirectory()) {
            throw new RuntimeException("指定的回单目录不存在: " + getBankReceiptDirectoryPath(bankCode));
        }

        return Arrays.stream(Objects.requireNonNull(directory.listFiles()))
                .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".pdf"))
                .map(file -> {
                    String fileName = file.getName();
                    Optional<BankReceipt> receiptOpt = receiptRepository.findByFileNameAndBankType(fileName, bankCode);

                    String status = "PENDING";
                    if (receiptOpt.isPresent()) {
                        status = receiptOpt.get().getStatus();
                    }

                    return new ReceiptFileDTO(
                            fileName,
                            getBankName(bankCode),
                            status,
                            formatFileSize(file.length()),
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(file.lastModified()))
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取所有银行的回单文件列表（按银行分组）
     */
    public Map<String, List<ReceiptFileDTO>> listAllBankReceiptFiles() {
        return bankReceiptPaths.keySet().stream()
                .collect(Collectors.toMap(
                        bankCode -> bankCode,
                        this::listBankReceiptFiles
                ));
    }

    /**
     * 根据银行代码获取银行名称
     */
    private String getBankName(String bankCode) {
        switch (bankCode.toUpperCase()) {
            case "CMBC": return "民生银行";
            case "SRCB": return "上海农商银行";
            case "CITIC": return "中信银行";
            default: return "其他银行";
        }
    }

    private String formatFileSize(long size) {
        if (size < 1024) return size + "B";
        else if (size < 1024 * 1024) return (size / 1024) + "KB";
        else return (size / (1024 * 1024)) + "MB";
    }
}