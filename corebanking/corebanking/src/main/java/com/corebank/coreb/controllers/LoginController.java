package com.corebank.coreb.controllers;

import com.corebank.coreb.dto.LoginRequestDTO;
import com.corebank.coreb.dto.LoginResponseDTO;
import com.corebank.coreb.entity.StaffUser;
import com.corebank.coreb.entity.RefreshToken;
import com.corebank.coreb.service.StaffUserService;
import com.corebank.coreb.service.RefreshTokenService;
import com.corebank.coreb.util.JwtUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    // -------------------------------------------------
    //  LOGIN – returns accessToken + sets refresh cookie
    // -------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest, HttpServletResponse response) {
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

            // 1️⃣ Generate Access Token
            String accessToken = jwtUtil.generateToken(
                    user.getUsername(),
                    user.getRole().name()
            );

            // 2️⃣ Generate Refresh Token
            RefreshToken refresh = refreshTokenService.createRefreshToken(user.getUsername());

            // 3️⃣ Send Refresh Token as HttpOnly Cookie
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refresh.getToken())
                    .httpOnly(true)
                    .secure(false) // ❗ change to true in production
                    .path("/api/auth")
                    .maxAge(7 * 24 * 60 * 60) // 7 days
                    .sameSite("Strict")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

            // 4️⃣ Response Body
            LoginResponseDTO res = new LoginResponseDTO(
                    "Login successful",
                    accessToken,
                    true
            );

            return ResponseEntity.ok(res);

        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest()
                    .body(new LoginResponseDTO(ex.getMessage(), null, false));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new LoginResponseDTO("Something went wrong. Please try again.", null, false));
        }
    }

    // -------------------------------------------------
    //  REFRESH – return new access token using cookie
    // -------------------------------------------------
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        try {

            // 1️⃣ Read refreshToken from cookie
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No cookies found"));
            }

            String refreshTokenValue = null;
            for (Cookie c : cookies) {
                if ("refreshToken".equals(c.getName())) {
                    refreshTokenValue = c.getValue();
                }
            }

            if (refreshTokenValue == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Refresh token missing"));
            }

            // 2️⃣ Validate Refresh Token from DB
            RefreshToken existingToken = refreshTokenService.findByToken(refreshTokenValue);
            refreshTokenService.verifyExpiration(existingToken);

            String username = existingToken.getUsername();

            // 3️⃣ Generate new access token
            String newAccessToken = jwtUtil.generateToken(username);

            // 4️⃣ Rotate refresh token for extra security
            RefreshToken newRefresh = refreshTokenService.createRefreshToken(username);

            ResponseCookie newRefreshCookie = ResponseCookie.from("refreshToken", newRefresh.getToken())
                    .httpOnly(true)
                    .secure(false) // ❗ switch to true on production
                    .path("/api/auth")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("Strict")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, newRefreshCookie.toString());

            // 5️⃣ Return new access token
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));

        } catch (Exception ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", ex.getMessage()));
        }
    }
}
