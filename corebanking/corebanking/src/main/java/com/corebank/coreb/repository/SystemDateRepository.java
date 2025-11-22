package com.corebank.coreb.repository;

import com.corebank.coreb.entity.SystemDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SystemDateRepository extends JpaRepository<SystemDate, Long> {
    // Fetch the most recently updated SystemDate entry
    Optional<SystemDate> findTopByOrderByIdDesc();
}
