package org.example.bankreceipt.controller;

import org.example.bankreceipt.dto.ApiResponse;
import org.example.bankreceipt.model.Detail;
import org.example.bankreceipt.service.DetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/details")
public class DetailController {

    @Autowired
    private DetailService DetailService;

    @GetMapping("/list")
    public ApiResponse<List<Detail>> getAllDetails() {
        return ApiResponse.success(DetailService.getAllDetails());
    }

    @GetMapping("/flow-no/{flowNo}")
    public ApiResponse<List<Detail>> getDetailsByFlowNo(@PathVariable String flowNo) {
        return ApiResponse.success(DetailService.getDetailsByFlowNo(flowNo));
    }

    @GetMapping("/pay-acc/{payAccNo}")
    public ApiResponse<List<Detail>> getDetailsByPayAccNo(@PathVariable String payAccNo) {
        return ApiResponse.success(DetailService.getDetailsByPayAccNo(payAccNo));
    }

    @GetMapping("/rcv-acc/{rcvAccNo}")
    public ApiResponse<List<Detail>> getDetailsByRcvAccNo(@PathVariable String rcvAccNo) {
        return ApiResponse.success(DetailService.getDetailsByRcvAccNo(rcvAccNo));
    }

    @GetMapping("/date-range")
    public ApiResponse<List<Detail>> getDetailsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        return ApiResponse.success(DetailService.getDetailsByDateRange(startDate, endDate));
    }

    @GetMapping("/receipt/{receiptFileName}")
    public ApiResponse<List<Detail>> getDetailsByReceiptFileName(@PathVariable String receiptFileName) {
        return ApiResponse.success(DetailService.getDetailsByReceiptFileName(receiptFileName));
    }

//    @GetMapping("/view/{id}")
//    public ApiResponse<Detail> getDetailById(@PathVariable Integer id) {
//        return ApiResponse.success(DetailService.getDetailById(id));
//    }

    // Controller修改
    @GetMapping("/view/{flowNo}")
    public ApiResponse<Detail> getDetailByFlowNo(@PathVariable String flowNo) {
        return ApiResponse.success(DetailService.getDetailByFlowNo(flowNo));
    }
}