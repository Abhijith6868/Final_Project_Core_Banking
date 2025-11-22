package com.corebank.coreb.repository;

import com.corebank.coreb.entity.Repayment;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RepaymentRepository extends JpaRepository<Repayment, Long> {

    // 🔹 Deactivate repayments linked to a loan
    @Modifying
    @Transactional
    @Query("UPDATE Repayment r SET r.status = 'Inactive' WHERE r.loan.loanId = :loanId")
    void deactivateRepaymentsByLoanId(@Param("loanId") Long loanId);

    // 🔹 Delete repayments linked to a loan
    @Modifying
    @Transactional
    @Query("DELETE FROM Repayment r WHERE r.loan.loanId = :loanId")
    void deleteByLoanId(@Param("loanId") Long loanId);

    // 🔹 Find all repayments for a specific loan
    List<Repayment> findByLoan_LoanId(Long loanId);

    // 🔹 Find all repayments for a specific customer (through loan)
    List<Repayment> findByLoan_Customer_CustomerId(Long customerId);

    // 🔹 Find repayments within a date range (used for report filters)
    List<Repayment> findByPaymentDateBetween(LocalDate startDate, LocalDate endDate);

    // 🔹 Find repayments for a specific branch
    @Query("SELECT r FROM Repayment r WHERE r.loan.branch.branchId = :branchid")
    List<Repayment> findByBranchId(@Param("branchId") Long branchId);

    // 🔹 Find unpaid bills due up to a specific date
    List<Repayment> findByBillingDoneFalseAndDueDateLessThanEqual(LocalDate date);
}
