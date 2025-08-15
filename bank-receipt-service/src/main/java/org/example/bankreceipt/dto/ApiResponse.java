package org.example.bankreceipt.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Integer status;  // 新增状态码字段

    // 私有构造方法
    private ApiResponse(boolean success, String message, T data, Integer status) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.status = status;
    }

    // 成功响应
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "success", data, HttpStatus.OK.value());
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, HttpStatus.OK.value());
    }

    // 错误响应（支持状态码）
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    public static <T> ApiResponse<T> error(HttpStatus status, String message) {
        return new ApiResponse<>(false, message, null, status.value());
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>(false, message, null, statusCode);
    }
}