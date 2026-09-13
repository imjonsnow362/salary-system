package com.salary.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String message) {
        super(message);
    }

    public static EmployeeNotFoundException forId(Long id) {
        return new EmployeeNotFoundException("No employee found with id " + id);
    }

    public static EmployeeNotFoundException forEmployeeId(String employeeId) {
        return new EmployeeNotFoundException("No employee found with employeeId " + employeeId);
    }
}
