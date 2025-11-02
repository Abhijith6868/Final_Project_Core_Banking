package com.corebank.coreb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.corebank.coreb.entity.Job;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    // You can add custom queries if needed, e.g.,
    // List<Job> findByJobType(String jobType);
}
