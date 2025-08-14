package org.example.bankreceipt.controller;

import org.example.bankreceipt.model.BankReceipt;
import org.example.bankreceipt.service.ReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/query")
public class QueryController {

    @Autowired
    private ReceiptService receiptService;

//    @GetMapping("/transaction/{transactionId}")
//    public ResponseEntity<List<BankReceipt>> getByTransactionId(@PathVariable String transactionId) {
//        return ResponseEntity.ok(receiptService.getReceiptsByTransactionId(transactionId));
//    }

    @GetMapping("/payer/{payerName}")
    public ResponseEntity<List<BankReceipt>> getByPayerName(@PathVariable String payerName) {
        return ResponseEntity.ok(receiptService.getReceiptsByPayerName(payerName));
    }

    @GetMapping("/receiver/{receiverName}")
    public ResponseEntity<List<BankReceipt>> getByReceiverName(@PathVariable String receiverName) {
        return ResponseEntity.ok(receiptService.getReceiptsByReceiverName(receiverName));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<BankReceipt>> getByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        return ResponseEntity.ok(receiptService.getReceiptsByDateRange(start, end));
    }

    @GetMapping("/bank/{bankType}")
    public ResponseEntity<List<BankReceipt>> getByBankType(@PathVariable String bankType) {
        return ResponseEntity.ok(receiptService.getReceiptsByBankType(bankType));
    }
}