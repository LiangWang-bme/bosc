package org.example.bankreceipt.service;

import org.example.bankreceipt.exception.ResourceNotFoundException;
import org.example.bankreceipt.model.BankReceipt;
import org.example.bankreceipt.repository.ReceiptRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ReceiptService {
    @Autowired
    private PdfParserService pdfParserService;
    @Autowired
    private ReceiptRepository receiptRepository;

    @Transactional
    public BankReceipt parseAndSaveReceipt(String filePath) throws IOException {
        // 1. 解析PDF文件
        BankReceipt receipt = pdfParserService.parsePdfToReceipt(filePath);

        // 2. 从文件路径提取文件名
        String fileName = new File(filePath).getName();
        receipt.setFileName(fileName);

        // 3. 检查交易流水号是否已存在
        if (receipt.getTransactionId() != null) {
            Optional<BankReceipt> existing = receiptRepository.findByTransactionId(
                    receipt.getTransactionId());

            if (existing.isPresent()) {
                // 更新现有记录而不是创建新记录
                BeanUtils.copyProperties(receipt, existing.get(),
                        "id", "createdAt");
                existing.get().setStatus("PARSED");
                return receiptRepository.save(existing.get());
            }
        }

        // 4. 保存新记录
        receipt.setStatus("PARSED");
        return receiptRepository.save(receipt);
    }

    public BankReceipt getReceiptByFileName(String fileName) {
        return receiptRepository.findByFileName(fileName)
                .orElseThrow(() -> new ResourceNotFoundException("回单不存在"));
    }

    public List<BankReceipt> getAllReceipts() {
        return receiptRepository.findAll();
    }

    public List<BankReceipt> getReceiptsByPayerName(String payerName) {
        return receiptRepository.findByPayerNameContaining(payerName);
    }

    public List<BankReceipt> getReceiptsByReceiverName(String receiverName) {
        return receiptRepository.findByReceiverNameContaining(receiverName);
    }

    public List<BankReceipt> getReceiptsByDateRange(Date startDate, Date endDate) {
        return receiptRepository.findByTransactionDateBetween(startDate, endDate);
    }

    public List<BankReceipt> getReceiptsByBankType(String bankType) {
        return receiptRepository.findByBankType(bankType);
    }
}
