package com.corebank.coreb.controllers;

import com.corebank.coreb.dto.LoginRequestDTO;
import com.corebank.coreb.dto.LoginResponseDTO;
import com.corebank.coreb.entity.StaffUser;
import com.corebank.coreb.service.StaffUserService;
import com.corebank.coreb.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            StaffUser user = staffUserService.authenticateUser(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            if (user == null) {
                return ResponseEntity
                        .badRequest()
                        .body(new LoginResponseDTO("Invalid username or password", null, false));
            }

            // ✅ Generate JWT token
            String token = jwtUtil.generateToken(user.getUsername());

            // ✅ Send token in response
            LoginResponseDTO response = new LoginResponseDTO(
                    "Login successful",
                    token,
                    true
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(new LoginResponseDTO(ex.getMessage(), null, false));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(new LoginResponseDTO("Something went wrong. Please try again.", null, false));
        }
    }
}
