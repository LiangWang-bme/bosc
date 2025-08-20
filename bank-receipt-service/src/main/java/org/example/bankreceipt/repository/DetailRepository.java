package org.example.bankreceipt.repository;

import org.example.bankreceipt.model.Detail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DetailRepository extends JpaRepository<Detail, Integer> {
    Optional<Detail> findFirstByFlowNo(String flowNo); // 返回单个结果的Optional
    List<Detail> findByBankCode(String bankCode);
}