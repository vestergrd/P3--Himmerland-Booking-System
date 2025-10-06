package com.auu_sw3_6.Himmerland_booking_software.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.auu_sw3_6.Himmerland_booking_software.api.model.JobExecutionLog;
import com.auu_sw3_6.Himmerland_booking_software.api.repository.JobExecutionLogRepository;

@Service
public class JobService {

  private final NotificationService notificationService;
  private final JobExecutionLogRepository jobExecutionLogRepository;

  @Autowired
  public JobService(NotificationService notificationService, JobExecutionLogRepository jobExecutionLogRepository) {
    this.notificationService = notificationService;
    this.jobExecutionLogRepository = jobExecutionLogRepository;
  }

  // Job for 05:00:00
  @Scheduled(cron = "0 0 5 * * *")
  public void run5AMJob() {
    executeJob("5AM Job");
  }

  // Job for 09:00:00
  @Scheduled(cron = "0 0 9 * * *")
  public void run9AMJob() {
    executeJob("9AM Job");
  }

  // Scheduled job for missed notifications (runs every 2 hours starting after
  // 5:00 AM)
  @Scheduled(cron = "0 0 6-23/2 * * *") // Runs at 6 AM, 8 AM, 10 AM, ... until 10 PM
  public void runMissedNotificationsJob() {
    LocalDate today = LocalDate.now();

    Boolean missedNotificationLog = jobExecutionLogRepository.existsByJobNameAndExecutionTimeAfter("Missed Droppoff Job", today.atStartOfDay());

    if (missedNotificationLog == false && LocalTime.now().isAfter(LocalTime.of(5, 0))) {
      System.out.println("Running missed notifications job...");
      notificationService.sendMissedNotifications();

      JobExecutionLog log = new JobExecutionLog();
      log.setJobName("Missed Droppoff Job");
      log.setExecutionTime(LocalDateTime.now());
      log.setStatus("COMPLETED");
      jobExecutionLogRepository.save(log);
    } else {
      System.out.println("Missed notifications job already run today.");
    }
  }

  private void executeJob(String jobName) {
    System.out.println("Running " + jobName);

    if (jobName.equals("5AM Job")) {
      notificationService.earlyMorningNotification();
    } else if (jobName.equals("9AM Job")) {
      notificationService.lateMorningNotification();
    }

    JobExecutionLog log = new JobExecutionLog();
    log.setJobName(jobName);
    log.setExecutionTime(LocalDateTime.now());
    log.setStatus("COMPLETED");
    jobExecutionLogRepository.save(log);
  }

  public void checkMissedJobsForToday() {
    LocalDate today = LocalDate.now();
    LocalTime currentTime = LocalTime.now();

    // Check if it's after 5 AM, before 7:30 AM, and if the 5 AM job has not been
    // completed today
    if (currentTime.isAfter(LocalTime.of(5, 0)) && currentTime.isBefore(LocalTime.of(7, 30))) {
      if (!jobExecutionLogRepository.existsByJobNameAndExecutionTimeAfter("5AM Job", today.atStartOfDay())) {
        run5AMJob();
      }
    }

    // Check if it's after 9 AM, before 12:00 PM, and if the 9 AM job has not been
    // completed today
    if (currentTime.isAfter(LocalTime.of(9, 0)) && currentTime.isBefore(LocalTime.of(12, 0))) {
      if (!jobExecutionLogRepository.existsByJobNameAndExecutionTimeAfter("9AM Job", today.atStartOfDay())) {
        run9AMJob();
      }
    }

    // Check if it's after 5 AM and the missed notifications job has not been completed today
    if (currentTime.isAfter(LocalTime.of(5, 0))) {
      runMissedNotificationsJob();
    }
  }
}
