package com.corebank.coreb.service;

import com.corebank.coreb.entity.Deposit;
import com.corebank.coreb.repository.DepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepositService {

    @Autowired
    private DepositRepository depositRepository;

    public Deposit save(Deposit deposit) {
        return depositRepository.save(deposit);
    }

    public Optional<Deposit> getById(Long id) {
        return depositRepository.findById(id);
    }

    public List<Deposit> getAll() {
        return depositRepository.findAll();
    }

 // Return boolean to indicate if delete was successful
    public boolean delete(Long id) {
        Optional<Deposit> depositOpt = depositRepository.findById(id);

        if (depositOpt.isPresent()) {
            Deposit deposit = depositOpt.get();

            // Only allow delete if status is "inactive" or "closed"
            if ("inactive".equalsIgnoreCase(deposit.getStatus()) ||
                "closed".equalsIgnoreCase(deposit.getStatus())) {

                depositRepository.deleteById(id);
                return true; // successfully deleted
            }
        }

        return false; // delete not allowed
    }
}
