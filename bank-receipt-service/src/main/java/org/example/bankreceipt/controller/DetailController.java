package org.example.bankreceipt.controller;

import org.example.bankreceipt.dto.ApiResponse;
import org.example.bankreceipt.exception.ResourceNotFoundException;
import org.example.bankreceipt.model.Detail;
import org.example.bankreceipt.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/details")
public class DetailController {

    @Autowired
    private DetailService DetailService;

    // 获取所有银行的明细列表
    @GetMapping("/list")
    public ApiResponse<List<Detail>> getAllDetails() {
        return ApiResponse.success(DetailService.getAllDetails());
    }

    // 获取指定银行的明细列表
    @GetMapping("/bank/{bankCode}")
    public ApiResponse<List<Detail>> getDetailsByBankCode(@PathVariable String bankCode) {
        try {
            List<Detail> details = DetailService.getDetailsByBankCode(bankCode);
            return ApiResponse.success("成功获取银行明细", details);
        } catch (Exception e) {
            return ApiResponse.error("获取银行明细失败: " + e.getMessage());
        }
    }

    // 查看明细详情
    @GetMapping("/view/{flowNo}")
    public ApiResponse<Detail> getDetailByFlowNo(@PathVariable String flowNo) {
        return ApiResponse.success(DetailService.getDetailByFlowNo(flowNo));
    }

    // 手动匹配
    @PostMapping("/manual-match/{flowNo}")
    public ApiResponse<Detail> manualMatchDetail(@PathVariable String flowNo) {
        try {
            Detail matchedDetail = DetailService.matchReceiptToDetail(flowNo);
            return ApiResponse.success("匹配成功", matchedDetail);
        } catch (ResourceNotFoundException e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error("匹配失败: " + e.getMessage());
        }
    }
}