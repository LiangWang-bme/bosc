package org.example.bankreceipt.service;

import org.example.bankreceipt.exception.ResourceNotFoundException;
import org.example.bankreceipt.model.Detail;
import org.example.bankreceipt.repository.DetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DetailService {

    @Autowired
    private DetailRepository DetailRepository;

    public List<Detail> getAllDetails() {
        return DetailRepository.findAll();
    }

    public List<Detail> getDetailsByFlowNo(String flowNo) {
        return DetailRepository.findByFlowNo(flowNo);
    }

    public List<Detail> getDetailsByPayAccNo(String payAccNo) {
        return DetailRepository.findByPayAccNo(payAccNo);
    }

    public List<Detail> getDetailsByRcvAccNo(String rcvAccNo) {
        return DetailRepository.findByRcvAccNo(rcvAccNo);
    }

    public List<Detail> getDetailsByDateRange(Date startDate, Date endDate) {
        return DetailRepository.findByTradeTimeBetween(startDate, endDate);
    }

    public List<Detail> getDetailsByReceiptFileName(String receiptFileName) {
        return DetailRepository.findByReceiptFileName(receiptFileName);
    }

//    public Detail getDetailById(Integer id) {
//        return DetailRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Account detail not found with id: " + id));
//    }

    public Detail getDetailByFlowNo(String flowNo) {
        return DetailRepository.findFirstByFlowNo(flowNo)
                .orElseThrow(() -> new ResourceNotFoundException("Account detail not found with flowNo: " + flowNo));
    }
}