package com.corebank.coreb.controllers;

import com.corebank.coreb.entity.StaffUser;
import com.corebank.coreb.service.StaffUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private StaffUserService staffUserService;

    // ✅ Approve a pending user
    @PostMapping("/approve/{id}")
    public ResponseEntity<?> approveUser(@PathVariable Long id) {
        StaffUser approvedUser = staffUserService.approveUser(id);
        return ResponseEntity.ok(Map.of(
            "message", "User approved successfully",
            "userId", approvedUser.getUserId(),
            "status", approvedUser.getStatus().toString()
        ));
    }

    // ✅ Reject a pending user
    @PostMapping("/reject/{id}")
    public ResponseEntity<?> rejectUser(@PathVariable Long id) {
        staffUserService.rejectUser(id);
        return ResponseEntity.ok("User rejected successfully");
    }
}
