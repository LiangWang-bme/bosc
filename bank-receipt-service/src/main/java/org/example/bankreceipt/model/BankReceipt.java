package org.example.bankreceipt.model;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "bank_receipts")
public class BankReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private Long fileSize;
    private String status; // PENDING/PARSED

    @Column(name = "transaction_date")
    private Date transactionDate;

    @Column(name = "transaction_id", unique = true)
    private String transactionId;

    @Column(name = "payer_account")
    private String payerAccount;

    @Column(name = "receiver_account")
    private String receiverAccount;

    @Column(name = "payer_name")
    private String payerName;

    @Column(name = "receiver_name")
    private String receiverName;

    @Column(name = "payer_bank")
    private String payerBank;

    @Column(name = "receiver_bank")
    private String receiverBank;

    private String currency;

    @Column(name = "amount_words")
    private String amountWords;

    @Column(name = "amount_numbers")
    private BigDecimal amountNumbers;

    @Column(name = "record_id")
    private String recordId;

    @Column(name = "voucher_id")
    private String voucherId;

    @Column(name = "bank_remark")
    private String bankRemark;

    @Column(name = "customer_remark")
    private String customerRemark;

    @Column(name = "login_id")
    private String loginId;

    @Column(name = "verification_code")
    private String verificationCode;

    @Column(name = "bank_type")
    private String bankType; // 新增字段，标识银行类型

    @Column(name = "created_at", updatable = false, insertable = false)
    private Date createdAt;
}
