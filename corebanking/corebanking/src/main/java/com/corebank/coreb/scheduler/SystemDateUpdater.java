package com.corebank.coreb.scheduler;

import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.corebank.coreb.entity.SystemDate;
import com.corebank.coreb.repository.SystemDateRepository;

@Component
public class SystemDateUpdater {

    @Autowired
    private SystemDateRepository systemDateRepository;

    // Runs every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void updateSystemDate() {
        SystemDate systemDate = systemDateRepository.findById(1L)
                .orElse(new SystemDate());

        systemDate.setCurrentDate(LocalDate.now());
        systemDate.setUpdatedAt(LocalDate.now());
        systemDate.setUpdatedBy("SYSTEM");

        systemDateRepository.save(systemDate);

        System.out.println("✅ System date updated to: " + LocalDate.now());
    }
}
