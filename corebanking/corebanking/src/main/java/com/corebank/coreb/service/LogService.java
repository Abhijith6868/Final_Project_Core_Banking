package com.corebank.coreb.service;

import com.corebank.coreb.entity.Log;
import com.corebank.coreb.repository.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public Log save(Log log) {
        return logRepository.save(log);
    }

    public Optional<Log> getById(Long id) {
        return logRepository.findById(id);
    }

    public List<Log> getAll() {
        return logRepository.findAll();
    }

    public void delete(Long id) {
        logRepository.deleteById(id);
    }
}
