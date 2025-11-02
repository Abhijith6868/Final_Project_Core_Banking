package com.corebank.coreb.service;

import com.corebank.coreb.entity.LoanTransaction;
import com.corebank.coreb.repository.LoanTransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoanTransactionService {

    @Autowired
    private LoanTransactionRepository loanTransactionRepository;

    // --------------------
    // Create new loan transaction
    // --------------------
    public LoanTransaction saveTransaction(LoanTransaction transaction) {
        transaction.setTransactionDate(LocalDateTime.now());
        if (transaction.getStatus() == null) {
            transaction.setStatus("Completed");
        }
        return loanTransactionRepository.save(transaction);
    }

    // --------------------
    // Get transaction by ID
    // --------------------
    public Optional<LoanTransaction> getTransactionById(Long transactionId) {
        return loanTransactionRepository.findById(transactionId);
    }

    // --------------------
    // Get all transactions
    // --------------------
    public List<LoanTransaction> getAllTransactions() {
        return loanTransactionRepository.findAll();
    }

    // --------------------
    // Get transactions by loan ID
    // --------------------
    public List<LoanTransaction> getTransactionsByLoan(Long loanId) {
        return loanTransactionRepository.findByLoan_LoanId(loanId);
    }

    // --------------------
    // Get transactions by customer ID
    // --------------------
    public List<LoanTransaction> getTransactionsByCustomer(Long customerId) {
        return loanTransactionRepository.findByCustomer_CustomerId(customerId);
    }
}
