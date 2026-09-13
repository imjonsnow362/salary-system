package com.salary.dto;

import com.salary.domain.Employee;
import com.salary.domain.EmploymentStatus;

import java.time.LocalDate;

public final class EmployeeResponse {

    private final Long id;
    private final String employeeId;
    private final String fullName;
    private final String email;
    private final String phoneNumber;
    private final String department;
    private final String designation;
    private final String countryName;
    private final LocalDate hiredDate;
    private final EmploymentStatus employmentStatus;

    private EmployeeResponse(Long id, String employeeId, String fullName, String email, String phoneNumber,
                              String department, String designation, String countryName,
                              LocalDate hiredDate, EmploymentStatus employmentStatus) {
        this.id = id;
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.department = department;
        this.designation = designation;
        this.countryName = countryName;
        this.hiredDate = hiredDate;
        this.employmentStatus = employmentStatus;
    }

    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(), employee.getEmployeeId(), employee.getFullName(),
                employee.getEmail(), employee.getPhoneNumber(), employee.getDepartment(),
                employee.getDesignation(), employee.getCountry().getName(),
                employee.getHiredDate(), employee.getEmploymentStatus());
    }

    public Long getId() {
        return id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDepartment() {
        return department;
    }

    public String getDesignation() {
        return designation;
    }

    public String getCountryName() {
        return countryName;
    }

    public LocalDate getHiredDate() {
        return hiredDate;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }
}
