package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@SpringBootApplication
/**
 * Main entry point for the Spring Boot application.
 *
 * @author Manideep Reddy Chinthareddy
 */
public class DemoApplication {

    /**
     * The main method to start the application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    /**
     * Creates a task executor for asynchronous processing.
     * This is used in R4J timelimiter
     *
     * @return the configured Executor
     */
    @Bean
    public Executor taskExecutor() {
        return new ThreadPoolTaskExecutor();
    }

}
