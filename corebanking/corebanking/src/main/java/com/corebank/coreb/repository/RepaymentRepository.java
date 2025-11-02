package com.corebank.coreb.repository;

import com.corebank.coreb.entity.Repayment;

import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Repayment r SET r.status = 'Inactive' WHERE r.loan.loanId = :loanId")
    void deactivateRepaymentsByLoanId(@Param("loanId") Long loanId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Repayment r WHERE r.loan.loanId = :loanId")
    void deleteByLoanId(@Param("loanId") Long loanId);
    
    List<Repayment> findByLoan_LoanId(Long loanId);
    List<Repayment> findByCustomer_CustomerId(Long customerId);
    
    List<Repayment> findByBillingDoneFalseAndDueDateLessThanEqual(LocalDate date);
}
