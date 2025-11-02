package com.corebank.coreb.service;

import com.corebank.coreb.entity.Loan;
import com.corebank.coreb.entity.Repayment;
import com.corebank.coreb.repository.LoanRepository;
import com.corebank.coreb.repository.RepaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RepaymentService {

    @Autowired
    private RepaymentRepository repaymentRepository;

    @Autowired
    private LoanRepository loanRepository;

    // --------------------
    // Save Repayment (manual insert, if needed)
    // --------------------
    public Repayment saveRepayment(Repayment repayment) {
        if (repayment.getStatus() == null) {
            repayment.setStatus("UNPAID");
        }
        if (repayment.getAmountPaid() == null) {
            repayment.setAmountPaid(BigDecimal.ZERO);
        }
        if (repayment.getRemainingPrincipal() == null && repayment.getLoan() != null) {
            repayment.setRemainingPrincipal(repayment.getLoan().getBalancePrincipal());
        }
        return repaymentRepository.save(repayment);
    }

    // --------------------
    // Process Payment (Partial / Full)
    // --------------------
    public Repayment processPayment(Long repaymentId, BigDecimal amountPaid) {
        Repayment repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new RuntimeException("Repayment not found"));

        Loan loan = repayment.getLoan();
        BigDecimal currentBalance = loan.getBalancePrincipal();

        // Calculate dynamic interest for this period
        BigDecimal monthlyRate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);
        BigDecimal interestDue = currentBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);

        // Split payment into interest and principal
        BigDecimal interestPaid = amountPaid.min(interestDue);
        BigDecimal principalPaid = amountPaid.subtract(interestPaid).max(BigDecimal.ZERO);

        // Update repayment
        repayment.setAmountPaid(amountPaid);
        repayment.setInterestPaid(interestPaid);
        repayment.setPrincipalPaid(principalPaid);
        repayment.setOutstandingInterest(interestDue.subtract(interestPaid).max(BigDecimal.ZERO));
        repayment.setRemainingPrincipal(currentBalance.subtract(principalPaid).max(BigDecimal.ZERO));
        repayment.setPaymentDate(LocalDate.now());

        // Update repayment status
        if (amountPaid.compareTo(repayment.getTotalDue()) >= 0) {
            repayment.setStatus("PAID");
        } else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            repayment.setStatus("PARTIAL");
        } else {
            repayment.setStatus("UNPAID");
        }

        // Update loan balance and status
        loan.setBalancePrincipal(repayment.getRemainingPrincipal());
        if (loan.getBalancePrincipal().compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus("CLOSED");
        }
        loanRepository.save(loan);

        return repaymentRepository.save(repayment);
    }

    // --------------------
    // Update Repayment (manual updates, e.g., admin corrections)
    // --------------------
    public Repayment updateRepayment(Repayment repayment) {
        Repayment existing = repaymentRepository.findById(repayment.getRepaymentId())
                .orElseThrow(() -> new RuntimeException("Repayment not found"));

        existing.setExpectedPrincipal(repayment.getExpectedPrincipal());
        existing.setExpectedInterest(repayment.getExpectedInterest());
        existing.setTotalDue(repayment.getTotalDue());
        existing.setAmountPaid(repayment.getAmountPaid());
        existing.setPrincipalPaid(repayment.getPrincipalPaid());
        existing.setInterestPaid(repayment.getInterestPaid());
        existing.setRemainingPrincipal(repayment.getRemainingPrincipal());
        existing.setOutstandingInterest(repayment.getOutstandingInterest());
        existing.setRateOfInterest(repayment.getRateOfInterest());
        existing.setStatus(repayment.getStatus());
        existing.setPaymentDate(repayment.getPaymentDate());
        existing.setReceiptNumber(repayment.getReceiptNumber());
        existing.setBillingDone(repayment.getBillingDone());

        return repaymentRepository.save(existing);
    }

    // --------------------
    // Get Repayment by ID
    // --------------------
    public Optional<Repayment> getRepaymentById(Long repaymentId) {
        return repaymentRepository.findById(repaymentId);
    }

    // --------------------
    // Get All Repayments
    // --------------------
    public List<Repayment> getAllRepayments() {
        return repaymentRepository.findAll();
    }

    // --------------------
    // Get Repayments by Loan ID
    // --------------------
    public List<Repayment> getRepaymentsByLoan(Long loanId) {
        return repaymentRepository.findByLoan_LoanId(loanId);
    }

    // --------------------
    // Get Repayments by Customer ID
    // --------------------
    public List<Repayment> getRepaymentsByCustomer(Long customerId) {
        return repaymentRepository.findByCustomer_CustomerId(customerId);
    }

    // --------------------
    // Delete Repayment (if not yet paid)
    // --------------------
    public boolean deleteRepayment(Long repaymentId) {
        Repayment repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new RuntimeException("Repayment not found"));

        if ("PAID".equalsIgnoreCase(repayment.getStatus())) {
            return false; // Prevent deletion of paid EMI
        }

        repaymentRepository.delete(repayment);
        return true;
    }
}
