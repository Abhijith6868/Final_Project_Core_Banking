package com.corebank.coreb.service;

import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuditService {
    public void log(String event, String username, String details) {
        // write to DB table or append to file. Basic print for now:
        System.out.printf("[%s] EVENT=%s user=%s details=%s%n", LocalDateTime.now(), event, username, details);
    }
}
