package com.corebank.coreb.service;

import com.corebank.coreb.entity.Notification;
import com.corebank.coreb.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    public Notification save(Notification notification) {
        return notificationRepository.save(notification);
    }

    public Optional<Notification> getById(Long id) {
        return notificationRepository.findById(id);
    }

    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    public void delete(Long id) {
        notificationRepository.deleteById(id);
    }
}
