package com.corebank.coreb.repository;

import com.corebank.coreb.entity.Billing;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingRepository extends JpaRepository<Billing, Long> {

	   // Find all billings for a specific loan
    List<Billing> findByLoan_LoanId(Long loanId);

    // Find all billings for a specific repayment
    List<Billing> findByRepayment_RepaymentId(Long repaymentId);
	
}
