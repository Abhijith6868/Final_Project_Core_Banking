package com.corebank.coreb.config;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

@WebListener
@Component
public class SessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        // Optionally log session creation
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // Called on invalidation or expiration
        String sessionId = se.getSession().getId();
        System.out.println("Session destroyed: " + sessionId + " at " + java.time.LocalDateTime.now());
        // TODO: call AuditService to record session end if you have one
    }
}
