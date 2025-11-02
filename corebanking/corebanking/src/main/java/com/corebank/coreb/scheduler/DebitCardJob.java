package com.corebank.coreb.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.corebank.coreb.entity.Job;
import com.corebank.coreb.repository.JobRepository;
import com.corebank.coreb.service.CardService;

@Service
public class DebitCardJob {

    @Autowired
    private CardService cardService;

    @Autowired
    private JobRepository jobRepository;

    @Scheduled(cron = "0 0 0 * * ?") // Runs daily at midnight
    public void expireDebitCards() {
        Job jobLog = new Job();
        jobLog.setJobType("DebitCardExpiryJob");
        jobLog.setStartTime(LocalDateTime.now());
        jobLog.setExecutionMode("Automatic");
        jobLog.setProcessedDate(LocalDate.now());

        try {
            int expiredCards = cardService.deactivateExpiredCards();
            jobLog.setStatus("Success");
            jobLog.setRemarks("Expired " + expiredCards + " debit cards.");
        } catch (Exception e) {
            jobLog.setStatus("Failed");
            jobLog.setRemarks("Error: " + e.getMessage());
        }

        jobLog.setEndTime(LocalDateTime.now());
        jobRepository.save(jobLog);
    }

}
