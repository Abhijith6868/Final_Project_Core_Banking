package com.corebank.coreb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.corebank.coreb.entity.Collateral;

@Repository
public interface CollateralRepository extends JpaRepository<Collateral, Long> {

    // OR if you prefer by loanId
    List<Collateral> findByLoan_LoanId(Long loanId);
}
