package com.auu_sw3_6.Himmerland_booking_software.api.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auu_sw3_6.Himmerland_booking_software.api.model.JobExecutionLog;
@Repository
public interface JobExecutionLogRepository extends JpaRepository<JobExecutionLog, Long> {

  boolean existsByJobNameAndExecutionTimeAfter(String jobName, LocalDateTime startOfDay);

}
