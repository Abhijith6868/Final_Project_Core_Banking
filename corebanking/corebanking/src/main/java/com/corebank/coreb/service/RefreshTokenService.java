package com.corebank.coreb.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.corebank.coreb.entity.RefreshToken;
import com.corebank.coreb.repository.RefreshTokenRepository;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {
 private final RefreshTokenRepository repo;
 private final long refreshTokenDurationSec;

 public RefreshTokenService(RefreshTokenRepository repo,
         @Value("${security.refresh-token-duration-sec:604800}") long refreshTokenDurationSec) {
     this.repo = repo;
     this.refreshTokenDurationSec = refreshTokenDurationSec; // default 7 days
 }

 public RefreshToken createRefreshToken(String username) {
     // remove existing token for user (optional)
     repo.deleteByUsername(username);

     RefreshToken t = new RefreshToken();
     t.setToken(UUID.randomUUID().toString());
     t.setUsername(username);
     t.setExpiryDate(Instant.now().plusSeconds(refreshTokenDurationSec));
     return repo.save(t);
 }

 public RefreshToken verifyExpiration(RefreshToken token) {
     if (token.getExpiryDate().isBefore(Instant.now())) {
         repo.delete(token);
         throw new RuntimeException("Refresh token expired, please login again");
     }
     return token;
 }

 public void deleteByUsername(String username) {
     repo.deleteByUsername(username);
 }

 public RefreshToken findByToken(String token) {
     return repo.findByToken(token).orElseThrow(() -> new RuntimeException("Refresh token not found"));
 }
}
