package com.corebank.coreb.service;

import com.corebank.coreb.entity.SystemDate;
import com.corebank.coreb.repository.SystemDateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SystemDateService {

    @Autowired
    private SystemDateRepository systemDateRepository;

    public LocalDate getSystemDate() {
        return systemDateRepository.findAll()
                .stream()
                .findFirst()
                .map(SystemDate::getCurrentDate)
                .orElse(LocalDate.now()); // fallback if empty
    }

    public void updateSystemDate(LocalDate newDate, String updatedBy) {
        SystemDate systemDate = systemDateRepository.findAll()
                .stream()
                .findFirst()
                .orElse(new SystemDate());

        systemDate.setCurrentDate(newDate);
        systemDate.setUpdatedBy(updatedBy);
        systemDate.setUpdatedAt(LocalDate.now());

        systemDateRepository.save(systemDate);
    }
}
