package com.corebank.coreb.service;

import com.corebank.coreb.dto.RepaymentReportDTO;
import com.corebank.coreb.entity.Loan;
import com.corebank.coreb.entity.Repayment;
import com.corebank.coreb.repository.LoanRepository;
import com.corebank.coreb.repository.RepaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class RepaymentService {

    private final RepaymentRepository repaymentRepository;
    private final LoanRepository loanRepository;
    private final SystemDateService systemDateService;

    // Inject new modern PDF service
    private final PdfReportService pdfReportService;

    // Status constants
    private static final String STATUS_UNPAID = "UNPAID";
    private static final String STATUS_PARTIAL = "PARTIAL";
    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_CLOSED = "CLOSED";

    // --------------------
    // Save Repayment
    // --------------------
    public Repayment saveRepayment(Repayment repayment) {
        if (repayment.getStatus() == null) repayment.setStatus(STATUS_UNPAID);
        if (repayment.getAmountPaid() == null) repayment.setAmountPaid(BigDecimal.ZERO);

        if (repayment.getRemainingPrincipal() == null && repayment.getLoan() != null) {
            repayment.setRemainingPrincipal(repayment.getLoan().getBalancePrincipal());
        }

        if (repayment.getPaymentDate() == null) {
            repayment.setPaymentDate(systemDateService.getSystemDate());
        }

        return repaymentRepository.save(repayment);
    }

    // --------------------
    // Process Payment
    // --------------------
    public Repayment processPayment(Long repaymentId, BigDecimal amountPaid) {

        Repayment repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new RuntimeException("Repayment not found"));

        Loan loan = repayment.getLoan();
        if (loan == null || loan.getInterestRate() == null || loan.getBalancePrincipal() == null) {
            throw new RuntimeException("Loan data missing for repayment");
        }

        BigDecimal currentBalance = loan.getBalancePrincipal();
        LocalDate systemDate = systemDateService.getSystemDate();

        // Calculate monthly interest
        BigDecimal monthlyRate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(12 * 100), 10, RoundingMode.HALF_UP);

        BigDecimal interestDue = currentBalance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal interestPaid = amountPaid.min(interestDue);
        BigDecimal principalPaid = amountPaid.subtract(interestPaid).max(BigDecimal.ZERO);

        repayment.setAmountPaid(amountPaid.setScale(2, RoundingMode.HALF_UP));
        repayment.setInterestPaid(interestPaid);
        repayment.setPrincipalPaid(principalPaid);
        repayment.setOutstandingInterest(interestDue.subtract(interestPaid).max(BigDecimal.ZERO));
        repayment.setRemainingPrincipal(currentBalance.subtract(principalPaid).max(BigDecimal.ZERO));
        repayment.setPaymentDate(systemDate);

        // Status update
        if (amountPaid.compareTo(repayment.getTotalDue()) >= 0) {
            repayment.setStatus(STATUS_PAID);
        } else if (amountPaid.compareTo(BigDecimal.ZERO) > 0) {
            repayment.setStatus(STATUS_PARTIAL);
        } else {
            repayment.setStatus(STATUS_UNPAID);
        }

        // Update Loan
        loan.setBalancePrincipal(repayment.getRemainingPrincipal().max(BigDecimal.ZERO));
        if (loan.getBalancePrincipal().compareTo(BigDecimal.ZERO) == 0) {
            loan.setStatus(STATUS_CLOSED);
        }

        loanRepository.save(loan);
        return repaymentRepository.save(repayment);
    }

    // --------------------
    // Update Repayment
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
        existing.setPaymentDate(systemDateService.getSystemDate());
        existing.setReceiptNumber(repayment.getReceiptNumber());
        existing.setBillingDone(repayment.getBillingDone());

        return repaymentRepository.save(existing);
    }

    // --------------------
    // Getters
    // --------------------
    public Optional<Repayment> getRepaymentById(Long repaymentId) {
        return repaymentRepository.findById(repaymentId);
    }

    public List<Repayment> getAllRepayments() {
        return repaymentRepository.findAll();
    }

    public List<Repayment> getRepaymentsByLoan(Long loanId) {
        return repaymentRepository.findByLoan_LoanId(loanId);
    }

    // --------------------
    // Delete Repayment
    // --------------------
    public boolean deleteRepayment(Long repaymentId) {
        Repayment repayment = repaymentRepository.findById(repaymentId)
                .orElseThrow(() -> new RuntimeException("Repayment not found"));

        if (STATUS_PAID.equalsIgnoreCase(repayment.getStatus())) {
            return false;
        }
        repaymentRepository.delete(repayment);
        return true;
    }

    // --------------------
    // Generate Report (DATA ONLY)
    // --------------------
    public List<RepaymentReportDTO> generateReport(Long loanId, LocalDate startDate, LocalDate endDate) {

        List<Repayment> repayments;

        if (loanId != null) {
            repayments = repaymentRepository.findByLoan_LoanId(loanId);
        } else if (startDate != null && endDate != null) {
            repayments = repaymentRepository.findByPaymentDateBetween(startDate, endDate);
        } else {
            repayments = repaymentRepository.findAll();
        }

        return repayments.stream().map(r -> new RepaymentReportDTO(
                r.getLoan().getLoanId(),
                r.getDueDate(),
                r.getPaymentDate(),
                r.getAmountPaid(),
                r.getRateOfInterest(),
                r.getRemainingPrincipal(),
                r.getOutstandingInterest()
        )).toList();
    }

    // --------------------
    // Modern PDF Export
    // --------------------
    public byte[] exportPdf(Long loanId, LocalDate startDate, LocalDate endDate, String generatedBy) {

        List<RepaymentReportDTO> reportData = generateReport(loanId, startDate, endDate);

        return pdfReportService.generatePdfReportModernBanking(reportData, generatedBy);
    }
}
