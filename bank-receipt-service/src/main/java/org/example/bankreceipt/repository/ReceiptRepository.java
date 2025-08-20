package org.example.bankreceipt.repository;

import org.example.bankreceipt.model.BankReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<BankReceipt, Long> {

    List<BankReceipt> findByTransactionDateAndRecordId(Date transactionDate, String recordId);

    Optional<BankReceipt> findByFileName(String fileName);
    Optional<BankReceipt> findByTransactionId(String transactionId);
    Optional<BankReceipt> findByFileNameAndBankType(String fileName, String bankType);

    List<BankReceipt> findByPayerNameContaining(String payerName);
    List<BankReceipt> findByReceiverNameContaining(String receiverName);
    List<BankReceipt> findByTransactionDateBetween(Date startDate, Date endDate);
    List<BankReceipt> findByBankType(String bankType);
}