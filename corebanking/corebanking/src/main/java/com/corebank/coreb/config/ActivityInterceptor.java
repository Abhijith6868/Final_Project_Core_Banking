package com.corebank.coreb.config;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.corebank.coreb.repository.StaffUserRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@Component
public class ActivityInterceptor implements HandlerInterceptor {

    private final StaffUserRepository staffUserRepository;

    public ActivityInterceptor(StaffUserRepository repo) {
        this.staffUserRepository = repo;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object uid = session.getAttribute("staffUserId");
            if (uid != null) {
                session.setAttribute("lastActivityAt", LocalDateTime.now());
                // Optionally persist lastActivityAt on user
                Long userId = (Long) uid;
                staffUserRepository.findById(userId).ifPresent(u -> {
                    u.setLastActivityAt(LocalDateTime.now());
                    staffUserRepository.save(u);
                });
            }
        }
        return true;
    }
}
