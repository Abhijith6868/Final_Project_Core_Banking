package com.corebank.coreb.controllers;

import com.corebank.coreb.entity.Log;
import com.corebank.coreb.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs") // ✅ keep consistent with other APIs
public class LogController {

    @Autowired
    private LogService logService;

    // ✅ Create new log
    @PostMapping
    public ResponseEntity<?> createLog(@RequestBody Log log) {
        try {
            Log saved = logService.save(log);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving log: " + e.getMessage());
        }
    }

    // ✅ Get all logs (admin only)
    @GetMapping
    public ResponseEntity<List<Log>> getAllLogs() {
        return ResponseEntity.ok(logService.getAll());
    }

    // ✅ Get log by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getLogById(@PathVariable Long id) {
        return logService.getById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.badRequest().body("Log not found"));
    }

}
