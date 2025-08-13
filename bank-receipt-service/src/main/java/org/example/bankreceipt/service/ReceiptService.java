package org.example.bankreceipt.service;


import org.example.bankreceipt.model.BankReceipt;
import org.example.bankreceipt.repository.ReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class ReceiptService {

    @Autowired
    private PdfParserService pdfParserService;

    @Autowired
    private ReceiptRepository receiptRepository;

    @Transactional
    public BankReceipt parseAndSaveReceipt(String filePath) throws IOException {
        BankReceipt receipt = pdfParserService.parsePdfToReceipt(filePath);
        return receiptRepository.save(receipt);
    }

    public List<BankReceipt> getAllReceipts() {
        return receiptRepository.findAll();
    }

    public List<BankReceipt> getReceiptsByTransactionId(String transactionId) {
        return receiptRepository.findByTransactionId(transactionId);
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
