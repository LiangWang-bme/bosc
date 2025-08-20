package org.example.bankreceipt.service;

import org.example.bankreceipt.exception.ResourceNotFoundException;
import org.example.bankreceipt.model.BankReceipt;
import org.example.bankreceipt.model.Detail;
import org.example.bankreceipt.repository.DetailRepository;
import org.example.bankreceipt.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class DetailService {

    @Autowired
    private DetailRepository DetailRepository;

    public List<Detail> getAllDetails() {
        return DetailRepository.findAll();
    }

    public List<Detail> getDetailsByBankCode(String bankCode) {
        return DetailRepository.findByBankCode(bankCode);
    }

    public Detail getDetailByFlowNo(String flowNo) {
        return DetailRepository.findFirstByFlowNo(flowNo)
                .orElseThrow(() -> new ResourceNotFoundException("Account detail not found with flowNo: " + flowNo));
    }

    @Autowired
    private ReceiptRepository receiptRepository;

    @Autowired
    private FileSystemReceiptService fileSystemReceiptService; // 添加这行

    public Detail matchReceiptToDetail(String flowNo) {
        Detail detail = getDetailByFlowNo(flowNo);
        String bankCode = detail.getBankCode(); // 假设Detail实体中有bankCode字段

        switch (bankCode.toUpperCase()) {
            case "CMBC": // 民生银行
                return matchCMBCReceipt(detail);
            case "SRCB": // 上海农商银行
                return matchSRCBReceipt(detail);
            case "CITIC": // 中信银行
                return matchCITICReceipt(detail);
            default:
                return matchDefaultReceipt(detail);
        }
    }

    private Detail matchCMBCReceipt(Detail detail) {
        // 获取民生银行回单目录
        File directory = new File(fileSystemReceiptService.getBankReceiptDirectoryPath("CMBC"));

//        规则1: 流水号+金额
//        String rule1FileName = detail.getFlowNo() + detail.getAmount().toString() + ".pdf";
        // 规则1: ORDER_NO + AMOUNT (如: 31301202307146230027043300.00.pdf)
        String amountStr = String.format("%.2f", detail.getAmount()); // 确保金额格式为两位小数
        String targetFileName = detail.getOrderNo() + amountStr + ".pdf";

        // 检查文件系统中是否存在匹配的回单
        File[] matchingFiles = directory.listFiles((dir, name) ->
                name.equalsIgnoreCase(targetFileName));

        if (matchingFiles != null && matchingFiles.length > 0) {
            detail.setReceiptFileName(matchingFiles[0].getName());
            return DetailRepository.save(detail);
        }

        throw new ResourceNotFoundException(
                "未找到匹配的民生银行回单，订单号: " + detail.getOrderNo() +
                        ", 金额: " + amountStr
        );
    }

    private Detail matchSRCBReceipt(Detail detail) {
        // 获取上海农商银行回单目录
        File directory = new File(fileSystemReceiptService.getBankReceiptDirectoryPath("SRCB"));

        // 规则2: 账号+日期+流水号 (如: 6225880123456789_20230818_12345678.pdf)
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = dateFormat.format(detail.getTradeTime());
        String targetFileName = detail.getPayAccNo() + "_" + formattedDate + "_" + detail.getFlowNo() + ".pdf";

        // 检查文件系统中是否存在匹配的回单
        File[] matchingFiles = directory.listFiles((dir, name) ->
                name.equalsIgnoreCase(targetFileName));

        if (matchingFiles != null && matchingFiles.length > 0) {
            detail.setReceiptFileName(matchingFiles[0].getName());
            return DetailRepository.save(detail);
        }

        throw new ResourceNotFoundException(
                "未找到匹配的上海农商银行回单，账号: " + detail.getPayAccNo() +
                        ", 日期: " + formattedDate +
                        ", 流水号: " + detail.getFlowNo()
        );
    }

    // 中信银行专用匹配方法
    private Detail matchCITICReceipt(Detail detail) {
        // 1. 首先尝试通过日期和核心流水号匹配
        List<BankReceipt> matchedReceipts = receiptRepository.findByTransactionDateAndRecordId(
                detail.getTradeTime(),
                detail.getCoreFlowNo() // 假设Detail实体中有coreFlowNo字段
        );

        if (!matchedReceipts.isEmpty()) {
            detail.setReceiptFileName(matchedReceipts.get(0).getFileName());
            return DetailRepository.save(detail);
        }

        // 2. 如果数据库匹配失败，尝试文件系统匹配
        try {
            return matchCITICFileSystem(detail);
        } catch (ResourceNotFoundException e) {
            throw new ResourceNotFoundException(
                    "未找到匹配的中信银行回单，交易日期: " +
                            new SimpleDateFormat("yyyy-MM-dd").format(detail.getTradeTime()) +
                            ", 核心流水号: " + detail.getCoreFlowNo()
            );
        }
    }

    // 中信银行文件系统匹配规则
    private Detail matchCITICFileSystem(Detail detail) {
        File directory = new File(fileSystemReceiptService.getBankReceiptDirectoryPath("CITIC"));

        // 中信银行文件名格式1: 账号_日期_流水号.pdf
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = dateFormat.format(detail.getTradeTime());
        String targetFileName = detail.getPayAccNo() + "_" + formattedDate + "_" + detail.getCoreFlowNo() + ".pdf";

        File[] matchingFiles = directory.listFiles((dir, name) ->
                name.equalsIgnoreCase(targetFileName));

        if (matchingFiles != null && matchingFiles.length > 0) {
            detail.setReceiptFileName(matchingFiles[0].getName());
            return DetailRepository.save(detail);
        }

        throw new ResourceNotFoundException("未找到匹配的回单文件");
    }

    private Detail matchDefaultReceipt(Detail detail) {
        // 尝试通用匹配规则
        File directory = new File(fileSystemReceiptService.getReceiptDirectoryPath());

        // 通用规则1: 流水号+金额
        String rule1FileName = detail.getFlowNo() + "_" +
                String.format("%.2f", detail.getAmount()) + ".pdf";

        // 通用规则2: 账号+日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String rule2FileName = detail.getPayAccNo() + "_" +
                dateFormat.format(detail.getTradeTime()) + ".pdf";

        File[] matchingFiles = directory.listFiles((dir, name) ->
                name.equalsIgnoreCase(rule1FileName) ||
                        name.equalsIgnoreCase(rule2FileName));

        if (matchingFiles != null && matchingFiles.length > 0) {
            detail.setReceiptFileName(matchingFiles[0].getName());
            return DetailRepository.save(detail);
        }

        throw new ResourceNotFoundException("未找到匹配的回单文件");
    }

}