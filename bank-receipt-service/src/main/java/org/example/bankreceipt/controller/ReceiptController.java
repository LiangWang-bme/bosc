package org.example.bankreceipt.controller;

import org.example.bankreceipt.dto.ApiResponse;
import org.example.bankreceipt.dto.ReceiptFileDTO;
import org.example.bankreceipt.exception.ResourceNotFoundException;
import org.example.bankreceipt.model.BankReceipt;
import org.example.bankreceipt.service.FileSystemReceiptService;
import org.example.bankreceipt.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    @Autowired
    private FileSystemReceiptService fileSystemReceiptService;

    public ReceiptController(FileSystemReceiptService fileSystemReceiptService) {
        this.fileSystemReceiptService = fileSystemReceiptService;
    }

    @GetMapping("/local-list")
    public ApiResponse<List<ReceiptFileDTO>> listLocalReceiptFiles() {
        try {
            List<ReceiptFileDTO> files = fileSystemReceiptService.listLocalReceiptFiles();
            return ApiResponse.success("成功获取本地回单列表", files);
        } catch (Exception e) {
            return ApiResponse.error("获取本地回单失败: " + e.getMessage());
        }
    }


    @Autowired
    private ReceiptService receiptService;

    @PostMapping("/parse/{fileName}")
    public ApiResponse<BankReceipt> parseReceipt(
            @PathVariable String fileName,
            @RequestParam(required = false) String filePath) {
        try {
            // 1. 获取本地回单目录路径
            String localDirPath = fileSystemReceiptService.getReceiptDirectoryPath();

            // 2. 构建完整文件路径
            Path fullPath = Paths.get(localDirPath, fileName);

            // 3. 验证文件路径安全性
            validateLocalFilePath(fullPath.toString());

            // 4. 解析并保存回单
            BankReceipt receipt = receiptService.parseAndSaveReceipt(fullPath.toString());

            return ApiResponse.success("本地回单解析成功", receipt);
        } catch (Exception e) {
            return ApiResponse.error("解析本地回单失败: " + e.getMessage());
        }

    }

    // 安全验证方法
    private void validateLocalFilePath(String filePath) throws IOException {
        Path path = Paths.get(filePath).normalize();
        Path allowedPath = Paths.get(fileSystemReceiptService.getReceiptDirectoryPath()).normalize();

        if (!path.startsWith(allowedPath)) {
            throw new SecurityException("非法访问路径: " + filePath);
        }

        if (!Files.exists(path)) {
            throw new FileNotFoundException("文件不存在: " + filePath);
        }

        if (!filePath.toLowerCase().endsWith(".pdf")) {
            throw new IllegalArgumentException("仅支持PDF文件");
        }
    }

    /**
     * 根据文件名获取回单详情
     * @param fileName 回单文件名
     * @return 回单详情
     */
    @GetMapping("/view/{fileName:.+}") // :.+ 允许文件名包含点
    public ApiResponse<BankReceipt> getReceiptByFileName(
            @PathVariable String fileName) {
        try {
            BankReceipt receipt = receiptService.getReceiptByFileName(fileName);
            return ApiResponse.success("成功获取回单详情", receipt);
        } catch (ResourceNotFoundException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("获取回单详情失败: " + e.getMessage());
        }
    }



    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<BankReceipt> uploadReceipt(@RequestParam("file") MultipartFile file) {
        try {
            // 确保上传目录存在
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 保存文件
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_DIR + file.getOriginalFilename());
            Files.write(path, bytes);

            // 解析并保存回单
            BankReceipt receipt = receiptService.parseAndSaveReceipt(path.toString());

            // 删除临时文件
            Files.deleteIfExists(path);

            return ResponseEntity.ok(receipt);
        } catch (IOException e) {
            // 替换原来的 internalServerError() 调用
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<BankReceipt>> getAllReceipts() {
        return ResponseEntity.ok(receiptService.getAllReceipts());
    }
}
