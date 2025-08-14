package org.example.bankreceipt.service;


import org.example.bankreceipt.model.BankReceipt;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PdfParserService {

    public BankReceipt parsePdfToReceipt(String filePath) throws IOException {
//        Map<String, String> extractedFields = parseBankReceipt(filePath);
//        return mapToBankReceipt(extractedFields);

        Map<String, String> extractedFields = parseBankReceipt(filePath);
        BankReceipt receipt = mapToBankReceipt(extractedFields);

        // 确保文件名被设置
        if (receipt.getFileName() == null) {
            receipt.setFileName(new File(filePath).getName());
        }

        return receipt;
    }

    public Map<String, String> parseBankReceipt(String pdfFilePath) throws IOException {
        Map<String, String> result = new HashMap<>();

        try (PDDocument document = PDDocument.load(new File(pdfFilePath))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            // 首先检测银行类型
            String bankType = detectBankType(text);
            result.put("bankType", bankType);

            // 根据银行类型选择不同的解析规则
            switch (bankType) {
                case "CMBC":
                    parseCMBC(text, result);
                    break;
                case "SRCB":
                    parseSRCB(text, result);
                    break;
                // 可以添加其他银行的解析方法
                default:
                    parseDefault(text, result);
            }
        }

        return result;
    }

    private String detectBankType(String text) {
        if (text.contains("上海农商银行")) {
            return "SRCB";
        } else if (text.contains("民生银行") || text.contains("CMBC")) {
            return "CMBC";
        }
        return "UNKNOWN";
    }

    private void parseCMBC(String text, Map<String, String> result) {
        // 民生银行解析规则
        Map<String, Pattern> fieldPatterns = new HashMap<>();
        fieldPatterns.put("交易日期", Pattern.compile("交易日期:\\s*(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})"));
        fieldPatterns.put("交易流水号", Pattern.compile("交易流水号:\\s*([0-9A-Z]+)"));
        fieldPatterns.put("付款人账号", Pattern.compile("付款人账号:\\s*(\\d+)"));
        fieldPatterns.put("收款人账号", Pattern.compile("收款人账号:\\s*(\\d+)"));
        fieldPatterns.put("付款人名称", Pattern.compile("付款人名称:\\s*(.+?)\\s"));
        fieldPatterns.put("收款人名称", Pattern.compile("收款人名称:\\s*(.+)"));
        fieldPatterns.put("付款人开户行", Pattern.compile("付款人开户行:\\s*(.+?)\\s"));
        fieldPatterns.put("收款人开户行", Pattern.compile("收款人开户行:\\s*(.+)"));
        fieldPatterns.put("币种", Pattern.compile("币种:\\s*(.+?)\\s"));
        fieldPatterns.put("金额(大写)", Pattern.compile("金额:\\(大写\\)(.+?)\\s"));
        fieldPatterns.put("金额(小写)", Pattern.compile("\\(小写\\)(.+?)\\s"));
        fieldPatterns.put("记账流水号", Pattern.compile("记账流水号:\\s*(\\d+)"));
        fieldPatterns.put("电子凭证号", Pattern.compile("电子凭证号:\\s*([0-9a-zA-Z]+)"));
        fieldPatterns.put("银行附言", Pattern.compile("银行附言:\\s*(.*?)(?=\\n|$)"));
        fieldPatterns.put("客户附言", Pattern.compile("客户附言:\\s*(.*?)(?=\\n|$)"));
        fieldPatterns.put("登录号", Pattern.compile("登录号:\\s*(\\d+)"));
        fieldPatterns.put("客户验证码", Pattern.compile("客户验证码:\\s*(\\d+)"));

        extractFields(text, result, fieldPatterns);
    }

    private void parseSRCB(String text, Map<String, String> result) {
        // 上海农商银行解析规则
        Map<String, Pattern> fieldPatterns = new HashMap<>();
        fieldPatterns.put("交易日期", Pattern.compile("交易日期：([0-9]{4}年[0-9]{2}月[0-9]{2}日)"));
        fieldPatterns.put("交易流水号", Pattern.compile("业务流水号：([0-9]+)"));
        fieldPatterns.put("付款人账号", Pattern.compile("付款人账号：([0-9]+)"));
        fieldPatterns.put("收款人账号", Pattern.compile("收款人账号：([0-9]+)"));
        fieldPatterns.put("付款人名称", Pattern.compile("付款人名称：(.+?)\\s"));
        fieldPatterns.put("收款人名称", Pattern.compile("收款人名称：(.+?)\\s"));
        fieldPatterns.put("付款人开户行", Pattern.compile("付款行名称：(.+?)\\s"));
        fieldPatterns.put("收款人开户行", Pattern.compile("收款行名称：(.+?)\\s"));

//        fieldPatterns.put("币种", Pattern.compile("小写 金额：([A-Z]+)"));
//        fieldPatterns.put("金额(小写)", Pattern.compile("小写 金额：[A-Z]+([0-9,.]+)"));
//        fieldPatterns.put("金额(大写)", Pattern.compile("大写 金额：(.+?)\\s"));

        // 修改金额相关正则表达式
        fieldPatterns.put("币种", Pattern.compile("(?:小写\\s*金额|金额\\s*小写)[：:]\\s*([A-Z]{2,3})"));
        fieldPatterns.put("金额(小写)", Pattern.compile("(?:小写\\s*金额|金额\\s*小写)[：:]\\s*[A-Z]{2,3}\\s*([0-9]+(?:,[0-9]{3})*(?:\\.[0-9]{2})?)"));
        fieldPatterns.put("金额(大写)", Pattern.compile("(?:大写\\s*金额|金额\\s*大写)[：:]\\s*(.+?)(?=\\s|$|\\n)"));

        fieldPatterns.put("银行附言", Pattern.compile("备注：(.+?)\\s"));

        extractFields(text, result, fieldPatterns);
    }

    private void parseDefault(String text, Map<String, String> result) {
        // 默认解析规则，适用于未知银行格式
        // 可以尝试一些通用的解析规则
    }

    private void extractFields(String text, Map<String, String> result, Map<String, Pattern> fieldPatterns) {
        for (Map.Entry<String, Pattern> entry : fieldPatterns.entrySet()) {
            Matcher matcher = entry.getValue().matcher(text);
            if (matcher.find()) {
                result.put(entry.getKey(), matcher.group(1).trim());
            }
        }
    }



    private BankReceipt mapToBankReceipt(Map<String, String> fields) {
        BankReceipt receipt = new BankReceipt();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat srcbDateFormat = new SimpleDateFormat("yyyy年MM月dd日");

            receipt.setBankType(fields.get("bankType"));

            // 根据银行类型处理日期格式
            if ("SRCB".equals(fields.get("bankType"))) {
                receipt.setTransactionDate(srcbDateFormat.parse(fields.get("交易日期")));
            } else {
                receipt.setTransactionDate(dateFormat.parse(fields.get("交易日期")));
            }

            receipt.setTransactionId(fields.get("交易流水号"));
            receipt.setPayerAccount(fields.get("付款人账号"));
            receipt.setReceiverAccount(fields.get("收款人账号"));
            receipt.setPayerName(fields.get("付款人名称"));
            receipt.setReceiverName(fields.get("收款人名称"));
            receipt.setPayerBank(fields.get("付款人开户行"));
            receipt.setReceiverBank(fields.get("收款人开户行"));
            receipt.setCurrency(fields.get("币种"));
            receipt.setAmountWords(fields.get("金额(大写)"));

            String amountStr = fields.get("金额(小写)");
            if (amountStr != null) {
                receipt.setAmountNumbers(new BigDecimal(amountStr.replace(",", "")));
            }

            receipt.setRecordId(fields.get("记账流水号"));
            receipt.setVoucherId(fields.get("电子凭证号"));
            receipt.setBankRemark(fields.get("银行附言"));
            receipt.setCustomerRemark(fields.get("客户附言"));
            receipt.setLoginId(fields.get("登录号"));
            receipt.setVerificationCode(fields.get("客户验证码"));
        } catch (Exception e) {
            throw new RuntimeException("Error mapping fields to BankReceipt", e);
        }

        return receipt;
    }

}