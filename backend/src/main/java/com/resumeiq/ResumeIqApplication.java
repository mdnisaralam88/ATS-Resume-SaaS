package com.resumeiq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ResumeIQ - AI-Powered ATS Resume Analyzer Platform
 * Main application entry point.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ResumeIqApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResumeIqApplication.class, args);
    }
}
