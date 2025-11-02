package com.corebank.coreb.scheduler;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.corebank.coreb.entity.Card;
import com.corebank.coreb.entity.Job;
import com.corebank.coreb.repository.JobRepository;
import com.corebank.coreb.service.CardService;

@Service
public class CreditCardJob {

    @Autowired
    private CardService cardService;

    @Autowired
    private JobRepository jobRepository;

    @Scheduled(cron = "0 0 1 1 * ?") // Runs monthly at 1 AM
    public void processCreditCards() {
        Job jobLog = new Job();
        jobLog.setJobType("CreditCardInterestJob");
        jobLog.setStartTime(LocalDateTime.now());
        jobLog.setExecutionMode("Automatic");
        jobLog.setProcessedDate(LocalDate.now());

        int processedCards = 0;

        try {
            List<Card> allCards = cardService.getAllCards();
            BigDecimal overdueRate = BigDecimal.valueOf(0.02); // 2% overdue
            LocalDate today = LocalDate.now();

            for (Card card : allCards) {
                if (!"Credit".equalsIgnoreCase(card.getCardType())
                        || !"active".equalsIgnoreCase(card.getStatus())) continue;

                // Expire card if needed
                if (card.getExpiryDate() != null && card.getExpiryDate().isBefore(today)) {
                    card.setStatus("inactive");
                    cardService.saveCard(card);
                    continue;
                }

                BigDecimal monthlyInterestRate = BigDecimal.ZERO;

                // Dynamic interest based on credit limit
                BigDecimal limit = card.getCreditLimit();
                if (limit != null) {
                    if (limit.compareTo(new BigDecimal("10000")) == 0) monthlyInterestRate = new BigDecimal("0.01");
                    else if (limit.compareTo(new BigDecimal("20000")) == 0) monthlyInterestRate = new BigDecimal("0.02");
                    else if (limit.compareTo(new BigDecimal("30000")) == 0) monthlyInterestRate = new BigDecimal("0.03");
                    else if (limit.compareTo(new BigDecimal("40000")) == 0) monthlyInterestRate = new BigDecimal("0.04");
                    else if (limit.compareTo(new BigDecimal("50000")) == 0) monthlyInterestRate = new BigDecimal("0.05");
                    else if (limit.compareTo(new BigDecimal("500000")) == 0) monthlyInterestRate = new BigDecimal("0.06");
                }

                BigDecimal interest = BigDecimal.ZERO;

                // Check overdue
                if (card.getDueDate() != null
                        && card.getDueDate().isBefore(today)
                        && card.getOutstandingBalance() != null
                        && card.getOutstandingBalance().compareTo(card.getMinimumPayment()) > 0) {

                    interest = card.getOutstandingBalance()
                            .multiply(overdueRate)
                            .setScale(2, RoundingMode.HALF_UP);
                } else if (card.getOutstandingBalance() != null) {
                    // Normal monthly interest
                    interest = card.getOutstandingBalance()
                            .multiply(monthlyInterestRate)
                            .setScale(2, RoundingMode.HALF_UP);
                }

                if (interest.compareTo(BigDecimal.ZERO) > 0) {
                    card.setOutstandingBalance(card.getOutstandingBalance().add(interest));
                }

                cardService.saveCard(card);
                processedCards++;
            }

            jobLog.setStatus("Success");
            jobLog.setRemarks("Processed " + processedCards + " credit cards.");
        } catch (Exception e) {
            jobLog.setStatus("Failed");
            jobLog.setRemarks("Error: " + e.getMessage());
        }

        jobLog.setEndTime(LocalDateTime.now());
        jobRepository.save(jobLog);
    }
}
