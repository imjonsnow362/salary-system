package com.salary.service.analytics;

import java.math.BigDecimal;

/** Average current base salary for one department, across all countries. */
public final class DepartmentSalaryStat {

    private final String department;
    private final BigDecimal averageSalary;
    private final long employeeCount;

    public DepartmentSalaryStat(String department, BigDecimal averageSalary, long employeeCount) {
        this.department = department;
        this.averageSalary = averageSalary;
        this.employeeCount = employeeCount;
    }

    public String getDepartment() {
        return department;
    }

    public BigDecimal getAverageSalary() {
        return averageSalary;
    }

    public long getEmployeeCount() {
        return employeeCount;
    }
}
