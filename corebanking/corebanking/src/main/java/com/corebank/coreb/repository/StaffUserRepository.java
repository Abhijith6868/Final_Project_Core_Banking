package com.corebank.coreb.repository;

import com.corebank.coreb.entity.StaffUser;
import com.corebank.coreb.enums.UserStatus; // ✅ make sure this import is here!
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface StaffUserRepository extends JpaRepository<StaffUser, Long> {

    Optional<StaffUser> findByUsername(String username);

    // ✅ This will now work correctly
    List<StaffUser> findByStatus(UserStatus status);
}
