package com.corebank.coreb.repository;

import com.corebank.coreb.entity.JobMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JobMasterRepository extends JpaRepository<JobMaster, Long> {

    Optional<JobMaster> findByJobName(String jobName);
}
