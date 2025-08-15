package org.example.bankreceipt.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "receipt")
@Data
public class ReceiptDirectoryConfig {

    private List<DirectoryConfig> directories;

    @Data
    public static class DirectoryConfig {
        private String path;     // 回单文件夹路径
        private String bankName; // 对应银行名称，如 民生银行 / 上海农商银行
    }
}