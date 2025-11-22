package com.corebank.coreb.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.corebank.coreb.entity.Job;
import com.corebank.coreb.entity.JobMaster;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    // You can add custom queries if needed, e.g.,
    // List<Job> findByJobType(String jobType);
	
	 // Get latest execution for a job
    Job findTopByJobMasterOrderBySeqNoDesc(JobMaster jobMaster);

    // Get full history
    List<Job> findByJobMasterOrderBySeqNoDesc(JobMaster jobMaster);
}
