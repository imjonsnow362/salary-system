package com.salary;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SalarySystemApplication: Main entry point for the Employee Salary Management System.
 *
 * Architecture:
 * - Monolithic Spring Boot application with REST API
 * - SQLite for data persistence (single file, no setup)
 * - JPA/Hibernate for ORM
 * - Layered architecture: Controller -> Service -> Repository -> Entity
 *
 * To run:
 *   mvn spring-boot:run
 *
 * To seed database with 10,000 employees:
 *   mvn exec:java -Dexec.mainClass="com.salary.config.DataSeeder"
 */
@SpringBootApplication
public class SalarySystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalarySystemApplication.class, args);
    }
}
