package com.auu_sw3_6.Himmerland_booking_software.api.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JobExecutionLogTest {

    @Test
    void gettersAndSetters_ShouldWorkCorrectly() {
        // Arrange
        JobExecutionLog jobExecutionLog = new JobExecutionLog();
        Long id = 1L;
        String jobName = "SampleJob";
        LocalDateTime executionTime = LocalDateTime.of(2034, 12, 2, 10, 30);
        String status = "COMPLETED";

        // Act
        jobExecutionLog.setId(id);
        jobExecutionLog.setJobName(jobName);
        jobExecutionLog.setExecutionTime(executionTime);
        jobExecutionLog.setStatus(status);

        // Assert
        assertEquals(id, jobExecutionLog.getId());
        assertEquals(jobName, jobExecutionLog.getJobName());
        assertEquals(executionTime, jobExecutionLog.getExecutionTime());
        assertEquals(status, jobExecutionLog.getStatus());
    }

    @Test
    void setNullValues_ShouldHandleGracefully() {
        // Arrange
        JobExecutionLog jobExecutionLog = new JobExecutionLog();

        // Act
        jobExecutionLog.setId(null);
        jobExecutionLog.setJobName(null);
        jobExecutionLog.setExecutionTime(null);
        jobExecutionLog.setStatus(null);

        // Assert
        assertNull(jobExecutionLog.getId());
        assertNull(jobExecutionLog.getJobName());
        assertNull(jobExecutionLog.getExecutionTime());
        assertNull(jobExecutionLog.getStatus());
    }

    @Test
    void defaultValues_ShouldBeNull() {
        // Act
        JobExecutionLog jobExecutionLog = new JobExecutionLog();

        // Assert
        assertNull(jobExecutionLog.getId());
        assertNull(jobExecutionLog.getJobName());
        assertNull(jobExecutionLog.getExecutionTime());
        assertNull(jobExecutionLog.getStatus());
    }
}
