package com.corebank.coreb.service;

import com.corebank.coreb.dto.StaffUserDTO;
import com.corebank.coreb.entity.Branch;
import com.corebank.coreb.entity.StaffUser;
import com.corebank.coreb.enums.UserStatus;
import com.corebank.coreb.repository.StaffUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StaffUserService implements UserDetailsService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_DURATION_MINUTES = 30;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // ✅ Used by Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        StaffUser user = staffUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new UsernameNotFoundException("User account is inactive.");
        }

        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            throw new UsernameNotFoundException("Account is locked.");
        }

        return new User(
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

    // ----------------------------------------------------
    // ✅ CREATE Staff User (Controller passes DTO)
    // ----------------------------------------------------
    public StaffUser saveStaffUser(StaffUserDTO dto) {
        if (dto.getPassword() == null || dto.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        StaffUser user = new StaffUser();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setRole(dto.getRole());
        user.setStatus(UserStatus.PENDING); // ✅ Always pending for approval
        user.setAccountLocked(dto.getAccountLocked() != null ? dto.getAccountLocked() : false);
        user.setFailedLoginAttempts(dto.getFailedLoginAttempts() != null ? dto.getFailedLoginAttempts() : 0);
        user.setCreatedBy(dto.getCreatedBy());
        user.setUpdatedBy(dto.getUpdatedBy());
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // ✅ Encode password before saving
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        // ✅ Link branch if branchId is provided
        if (dto.getBranchId() != null) {
            Branch branch = new Branch();
            branch.setBranchId(dto.getBranchId());
            user.setBranch(branch);
        }

        return staffUserRepository.save(user);
    }
    
	 // ----------------------------------------------------
	 // ✅ USER APPROVAL WORKFLOW (Request, Pending, Approve, Reject)
	 // ----------------------------------------------------
	
	 public StaffUser requestUserCreation(StaffUserDTO dto) {
	     dto.setStatus(UserStatus.PENDING);
	     dto.setAccountLocked(true);
	     return saveStaffUser(dto);
	 }
	
	 public List<StaffUser> getPendingUsers() {
	     return staffUserRepository.findByStatus(UserStatus.PENDING);
	 }
	
	 public StaffUser approveUser(Long id) {
	     StaffUser user = staffUserRepository.findById(id)
	             .orElseThrow(() -> new RuntimeException("User not found"));
	     user.setStatus(UserStatus.ACTIVE);
	     user.setAccountLocked(false);
	     user.setUpdatedAt(LocalDateTime.now());
	     return staffUserRepository.save(user);
	 }
	
	 public StaffUser rejectUser(Long id) {
	     StaffUser user = staffUserRepository.findById(id)
	             .orElseThrow(() -> new RuntimeException("User not found"));
	     user.setStatus(UserStatus.REJECTED);
	     user.setUpdatedAt(LocalDateTime.now());
	     return staffUserRepository.save(user);
	 }

    // ----------------------------------------------------
    // ✅ UPDATE Staff User (Controller passes entity)
    // ----------------------------------------------------
    public StaffUser updateStaffUser(StaffUser user) {
        StaffUser existing = staffUserRepository.findById(user.getUserId())
                .orElseThrow(() -> new RuntimeException("Staff user not found"));

        existing.setUsername(user.getUsername());
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setEmail(user.getEmail());
        existing.setPhoneNumber(user.getPhoneNumber());
        existing.setRole(user.getRole());
        existing.setBranch(user.getBranch());
        existing.setUpdatedAt(LocalDateTime.now());
        existing.setUpdatedBy(user.getUpdatedBy());

        if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
            existing.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        if (user.getStatus() != null) {
            existing.setStatus(user.getStatus());
        }

        return staffUserRepository.save(existing);
    }

    // ----------------------------------------------------
    // ✅ READ / DEACTIVATE / AUTHENTICATION
    // ----------------------------------------------------
    public Optional<StaffUser> getStaffUserById(Long userId) {
        return staffUserRepository.findById(userId);
    }

    public List<StaffUser> getAllStaffUsers() {
        return staffUserRepository.findAll();
    }

    public boolean deactivateStaffUser(Long userId) {
        StaffUser user = staffUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Staff user not found"));
        user.setStatus(UserStatus.SUSPENDED);
        user.setUpdatedAt(LocalDateTime.now());
        staffUserRepository.save(user);
        return true;
    }

    // ✅ Manual authentication
    public StaffUser authenticateUser(String username, String rawPassword) {
        Optional<StaffUser> opt = staffUserRepository.findByUsername(username);
        if (opt.isEmpty()) return null;

        StaffUser user = opt.get();

        if (Boolean.TRUE.equals(user.getAccountLocked())) {
            if (user.getLockTimestamp() != null) {
                LocalDateTime unlockAt = user.getLockTimestamp().plusMinutes(LOCK_DURATION_MINUTES);
                if (LocalDateTime.now().isAfter(unlockAt)) {
                    user.setAccountLocked(false);
                    user.setFailedLoginAttempts(0);
                    user.setLockTimestamp(null);
                    staffUserRepository.save(user);
                } else {
                    throw new RuntimeException("Account is locked. Try later.");
                }
            } else {
                throw new RuntimeException("Account is locked. Contact admin.");
            }
        }

        if (user.getStatus() == UserStatus.SUSPENDED) {
            throw new RuntimeException("User inactive.");
        }

        if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            user.setFailedLoginAttempts(0);
            user.setLastLogin(LocalDateTime.now());
            user.setLastActivityAt(LocalDateTime.now());
            staffUserRepository.save(user);
            return user;
        } else {
            int attempts = (user.getFailedLoginAttempts() == null) ? 0 : user.getFailedLoginAttempts();
            attempts++;
            user.setFailedLoginAttempts(attempts);
            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setAccountLocked(true);
                user.setLockTimestamp(LocalDateTime.now());
            }
            staffUserRepository.save(user);
            return null;
        }
    }

    public void unlockAccount(Long userId) {
        StaffUser user = staffUserRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Staff user not found"));
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockTimestamp(null);
        staffUserRepository.save(user);
    }
}
