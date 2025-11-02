package com.corebank.coreb.service;

import com.corebank.coreb.entity.Transaction;
import com.corebank.coreb.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public Transaction save(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getById(Long id) {
        return transactionRepository.findById(id);
    }

    public List<Transaction> getAll() {
        return transactionRepository.findAll();
    }

    public void delete(Long id) {
        transactionRepository.deleteById(id);
    }
}
