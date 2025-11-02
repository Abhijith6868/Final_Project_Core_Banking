package com.corebank.coreb.repository;

import com.corebank.coreb.entity.LoanTransaction;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long> {
	
	List<LoanTransaction> findByLoan_LoanId(Long loanId);

    List<LoanTransaction> findByCustomer_CustomerId(Long customerId);
}
