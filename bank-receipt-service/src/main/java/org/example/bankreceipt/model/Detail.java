package org.example.bankreceipt.model;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "us_acc_detail")
public class Detail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "TENANT_ID")
    private String tenantId;

    @Column(name = "FLOW_NO")
    private String flowNo;

    @Column(name = "ORDER_NO")
    private String orderNo;

    @Column(name = "TRADE_TIME")
    private Date tradeTime;

    @Column(name = "PAY_ACC_NO")
    private String payAccNo;

    @Column(name = "PAY_ACC_NAME")
    private String payAccName;

    @Column(name = "PAY_BANK_NO")
    private String payBankNo;

    @Column(name = "RCV_ACC_NO")
    private String rcvAccNo;

    @Column(name = "RCV_ACC_NAME")
    private String rcvAccName;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "SUMMARY")
    private String summary;

    @Column(name = "REMARK")
    private String remark;

    @Column(name = "PURPOSE")
    private String purpose;

    @Column(name = "VOUCHER_NO")
    private String voucherNo;

    @Column(name = "CORE_FLOW_NO")
    private String coreFlowNo;

    @Column(name = "BALANCE")
    private BigDecimal balance;

    @Column(name = "DEBIT_LOAN_FLAG")
    private String debitLoanFlag;

    @Column(name = "QUERY_DATE")
    private Date queryDate;

    @Column(name = "FILE_NAME")
    private String fileName;

    @Column(name = "FILE_PATH")
    private String filePath;

    @Column(name = "RECEIPT_FILE_NAME")
    private String receiptFileName;

    @Column(name = "BANK_LOGID")
    private String bankLogId;

    @Column(name = "CURRENCY")
    private String currency;

    @Column(name = "ACC_MONO")
    private String accMono;

    @Column(name = "BANK_NO")
    private String bankNo;

    @Column(name = "RCVAMT")
    private BigDecimal rcvAmt;

    @Column(name = "PAYAMT")
    private BigDecimal payAmt;

    @Column(name = "FEE_AMT")
    private BigDecimal feeAmt;

    @Column(name = "OPP_CUR_CODE")
    private String oppCurCode;

    @Column(name = "OPP_ACNAME")
    private String oppAcName;

    @Column(name = "OPP_BANKNO")
    private String oppBankNo;

    @Column(name = "OPP_BANKNAME")
    private String oppBankName;

    @Column(name = "TR_NATR_BANKNO")
    private String trNatrBankNo;

    @Column(name = "BANK_NAME")
    private String bankName;

    @Column(name = "HOST_SERIAL_NO")
    private String hostSerialNo;

    @Column(name = "TR_BANKNAME")
    private String trBankName;

    @Column(name = "TR_NAME")
    private String trName;

    @Column(name = "TR_CODE")
    private String trCode;

    @Column(name = "CERT_BATCHNO")
    private String certBatchNo;

    @Column(name = "POST_SCRIPT")
    private String postScript;

    @Column(name = "CREATE_BY")
    private String createBy;

    @Column(name = "CREATE_TIME")
    private Date createTime;

    @Column(name = "UPDATE_BY")
    private String updateBy;

    @Column(name = "UPDATE_TIME")
    private Date updateTime;
}