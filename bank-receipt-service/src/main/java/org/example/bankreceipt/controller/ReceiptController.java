package org.example.bankreceipt.controller;

import org.example.bankreceipt.dto.ApiResponse;
import org.example.bankreceipt.dto.ReceiptFileDTO;
import org.example.bankreceipt.exception.ResourceNotFoundException;
import org.example.bankreceipt.model.BankReceipt;
import org.example.bankreceipt.service.FileSystemReceiptService;
import org.example.bankreceipt.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {
    @Autowired
    private FileSystemReceiptService fileSystemReceiptService;
    @Autowired
    private ReceiptService receiptService;

    /**
     * 获取指定银行的回单文件列表
     * @param bankCode 银行代码 (CMBC, SRCB, CITIC)
     */
    @GetMapping("/bank/{bankCode}/list")
    public ApiResponse<List<ReceiptFileDTO>> listBankReceiptFiles(
            @PathVariable String bankCode) {
        try {
            List<ReceiptFileDTO> files = fileSystemReceiptService.listBankReceiptFiles(bankCode);
            return ApiResponse.success("成功获取银行回单列表", files);
        } catch (Exception e) {
            return ApiResponse.error("获取银行回单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有银行的回单文件列表（按银行分组）
     */
    @GetMapping("/all-banks/list")
    public ApiResponse<Map<String, List<ReceiptFileDTO>>> listAllBankReceiptFiles() {
        try {
            Map<String, List<ReceiptFileDTO>> allFiles = fileSystemReceiptService.listAllBankReceiptFiles();
            return ApiResponse.success("成功获取所有银行回单列表", allFiles);
        } catch (Exception e) {
            return ApiResponse.error("获取银行回单列表失败: " + e.getMessage());
        }
    }

    /**
     * 解析指定银行的回单
     */
    @PostMapping("/bank/{bankCode}/parse/{fileName}")
    public ApiResponse<BankReceipt> parseBankReceipt(
            @PathVariable String bankCode,
            @PathVariable String fileName) {
        try {
            // 1. 获取银行专属目录路径
            String bankDirPath = fileSystemReceiptService.getBankReceiptDirectoryPath(bankCode);

            // 2. 构建完整文件路径
            Path fullPath = Paths.get(bankDirPath, fileName);

            // 3. 验证文件路径安全性
            validateBankFilePath(fullPath.toString(), bankDirPath);

            // 4. 解析并保存回单
            BankReceipt receipt = receiptService.parseAndSaveReceipt(fullPath.toString());
            receipt.setBankType(bankCode); // 设置银行类型

            return ApiResponse.success("银行回单解析成功", receipt);
        } catch (Exception e) {
            return ApiResponse.error("解析银行回单失败: " + e.getMessage());
        }
    }

    /**
     * 预览指定银行的回单PDF
     */
    @GetMapping("/bank/{bankCode}/preview/{fileName:.+}")
    public ResponseEntity<byte[]> previewBankReceipt(
            @PathVariable String bankCode,
            @PathVariable String fileName) throws IOException {

        // 1. 获取银行专属路径
        String bankDirPath = fileSystemReceiptService.getBankReceiptDirectoryPath(bankCode);
        String fullPath = bankDirPath + File.separator + fileName;

        // 2. 验证路径安全性
        Path path = Paths.get(fullPath).normalize();
        if (!path.startsWith(Paths.get(bankDirPath).normalize())) {
            throw new SecurityException("非法访问路径");
        }

        // 3. 读取文件
        byte[] pdfBytes = Files.readAllBytes(path);

        // 4. 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(
                ContentDisposition.builder("inline")
                        .filename(fileName).build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    /**
     * 银行文件路径安全验证
     */
    private void validateBankFilePath(String filePath, String allowedBasePath) throws IOException {
        Path path = Paths.get(filePath).normalize();
        Path allowedPath = Paths.get(allowedBasePath).normalize();

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

//    // 获取回单列表
//    @GetMapping("/local-list")
//    public ApiResponse<List<ReceiptFileDTO>> listLocalReceiptFiles() {
//        try {
//            List<ReceiptFileDTO> files = fileSystemReceiptService.listLocalReceiptFiles();
//            return ApiResponse.success("成功获取本地回单列表", files);
//        } catch (Exception e) {
//            return ApiResponse.error("获取本地回单失败: " + e.getMessage());
//        }
//    }

//    // 回单解析
//    @PostMapping("/parse/{fileName}")
//    public ApiResponse<BankReceipt> parseReceipt(
//            @PathVariable String fileName,
//            @RequestParam(required = false) String filePath) {
//        try {
//            // 1. 获取本地回单目录路径
//            String localDirPath = fileSystemReceiptService.getReceiptDirectoryPath();
//
//            // 2. 构建完整文件路径
//            Path fullPath = Paths.get(localDirPath, fileName);
//
//            // 3. 验证文件路径安全性
//            validateLocalFilePath(fullPath.toString());
//
//            // 4. 解析并保存回单
//            BankReceipt receipt = receiptService.parseAndSaveReceipt(fullPath.toString());
//
//            return ApiResponse.success("本地回单解析成功", receipt);
//        } catch (Exception e) {
//            return ApiResponse.error("解析本地回单失败: " + e.getMessage());
//        }
//
//    }
//
//    // 安全验证方法
//    private void validateLocalFilePath(String filePath) throws IOException {
//        Path path = Paths.get(filePath).normalize();
//        Path allowedPath = Paths.get(fileSystemReceiptService.getReceiptDirectoryPath()).normalize();
//
//        if (!path.startsWith(allowedPath)) {
//            throw new SecurityException("非法访问路径: " + filePath);
//        }
//
//        if (!Files.exists(path)) {
//            throw new FileNotFoundException("文件不存在: " + filePath);
//        }
//
//        if (!filePath.toLowerCase().endsWith(".pdf")) {
//            throw new IllegalArgumentException("仅支持PDF文件");
//        }
//    }
//
//    // pdf预览
//    @GetMapping("/preview/{fileName:.+}")
//    public ResponseEntity<byte[]> previewReceipt(@PathVariable String fileName) throws IOException {
//        // 获取文件路径
//        String filePath = fileSystemReceiptService.getReceiptDirectoryPath() + File.separator + fileName;
//
//        // 验证文件路径安全性
//        Path path = Paths.get(filePath).normalize();
//        if (!path.startsWith(Paths.get(fileSystemReceiptService.getReceiptDirectoryPath()))) {
//            throw new SecurityException("非法访问路径");
//        }
//
//        // 读取PDF文件
//        byte[] pdfBytes = Files.readAllBytes(path);
//
//        // 设置响应头
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDisposition(ContentDisposition.builder("inline")
//                .filename(fileName).build());
//
//        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
//    }


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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<BankReceipt>> getAllReceipts() {
        return ResponseEntity.ok(receiptService.getAllReceipts());
    }
}