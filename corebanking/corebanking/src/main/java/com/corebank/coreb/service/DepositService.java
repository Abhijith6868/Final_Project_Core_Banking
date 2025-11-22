package com.corebank.coreb.service;

import com.corebank.coreb.entity.Deposit;
import com.corebank.coreb.repository.DepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepositService {

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private SystemDateService systemDateService; // ✅ Use central system date

    // --------------------
    // Create or Update Deposit
    // --------------------
    public Deposit save(Deposit deposit) {
        return depositRepository.save(deposit);
    }

    // --------------------
    // Get by ID
    // --------------------
    public Optional<Deposit> getById(Long id) {
        return depositRepository.findById(id);
    }

    // --------------------
    // Get All Deposits
    // --------------------
    public List<Deposit> getAll() {
        return depositRepository.findAll();
    }

    // --------------------
    // Safe Delete Deposit (Only if status = inactive/closed)
    // --------------------
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

    // --------------------
    // Auto-Close Matured Deposits (based on system date)
    // --------------------
    public int autoCloseMaturedDeposits() {
        LocalDate systemDate = systemDateService.getSystemDate();

        List<Deposit> deposits = depositRepository.findAll();
        int closedCount = 0;

        for (Deposit deposit : deposits) {
            if (deposit.getMaturityDate() != null
                    && !("closed".equalsIgnoreCase(deposit.getStatus()))
                    && !("inactive".equalsIgnoreCase(deposit.getStatus()))
                    && (systemDate.isAfter(deposit.getMaturityDate()) || systemDate.isEqual(deposit.getMaturityDate()))) {

                deposit.setStatus("closed");
                depositRepository.save(deposit);
                closedCount++;
            }
        }

        return closedCount; // return how many were closed automatically
    }
}
