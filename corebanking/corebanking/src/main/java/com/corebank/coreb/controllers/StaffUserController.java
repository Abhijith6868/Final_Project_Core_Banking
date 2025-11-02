package com.corebank.coreb.controllers;

import com.corebank.coreb.dto.StaffUserDTO;
import com.corebank.coreb.entity.StaffUser;
import com.corebank.coreb.enums.UserStatus;
import com.corebank.coreb.service.StaffUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
public class StaffUserController {

    @Autowired
    private StaffUserService staffUserService;

    // ✅ Create new staff
    @PostMapping("/create")
    public ResponseEntity<?> createStaffUser(@RequestBody StaffUserDTO staffUserDTO) {
        try {
            StaffUser saved = staffUserService.saveStaffUser(staffUserDTO);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating staff: " + e.getMessage());
        }
    }

    // ✅ Staff request creation for approval
    @PostMapping("/request")
    public ResponseEntity<?> requestUserCreation(@RequestBody StaffUserDTO dto) {
        dto.setStatus(UserStatus.PENDING);
        dto.setAccountLocked(true);
        StaffUser saved = staffUserService.saveStaffUser(dto);
        return ResponseEntity.ok("User request submitted for approval");
    }

    // ✅ Get all pending requests (visible to admin only but left here for easy reference)
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingUsers() {
        return ResponseEntity.ok(staffUserService.getPendingUsers());
    }

    // ✅ Update staff details
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateStaffUser(@PathVariable Long id, @RequestBody StaffUser user) {
        try {
            user.setUserId(id);
            StaffUser updated = staffUserService.updateStaffUser(user);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating staff: " + e.getMessage());
        }
    }

    // ✅ Get all staff
    @GetMapping("/all")
    public ResponseEntity<List<StaffUser>> getAllStaffUsers() {
        return ResponseEntity.ok(staffUserService.getAllStaffUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStaffUser(@PathVariable Long id) {
        var userOpt = staffUserService.getStaffUserById(id);
        return userOpt.<ResponseEntity<?>>map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.badRequest().body("Staff user not found"));
    }

    // ✅ Deactivate staff user
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivateStaffUser(@PathVariable Long id) {
        try {
            staffUserService.deactivateStaffUser(id);
            return ResponseEntity.ok("Staff user deactivated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deactivating user: " + e.getMessage());
        }
    }

    // ✅ Unlock staff account
    @PutMapping("/unlock/{id}")
    public ResponseEntity<?> unlockAccount(@PathVariable Long id) {
        try {
            staffUserService.unlockAccount(id);
            return ResponseEntity.ok("Account unlocked successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error unlocking account: " + e.getMessage());
        }
    }
}
