package com.salary.service;

import com.salary.domain.Employee;
import com.salary.domain.Salary;
import com.salary.exception.EmployeeNotFoundException;
import com.salary.repository.EmployeeRepository;
import com.salary.repository.SalaryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeRepository employeeRepository;

    public SalaryService(SalaryRepository salaryRepository, EmployeeRepository employeeRepository) {
        this.salaryRepository = salaryRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public Salary addSalary(Long employeeId, BigDecimal baseSalary, String currencyCode,
                             BigDecimal annualBonus, BigDecimal benefitsValue, LocalDate effectiveDate) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> EmployeeNotFoundException.forId(employeeId));

        if (baseSalary == null || baseSalary.signum() <= 0) {
            throw new IllegalArgumentException("Base salary must be positive");
        }
        if (effectiveDate.isBefore(employee.getHiredDate())) {
            throw new IllegalArgumentException("Salary effective date cannot be before employee's hire date");
        }

        Salary salary = new Salary(employee, baseSalary, currencyCode, annualBonus, benefitsValue, effectiveDate);
        return salaryRepository.save(salary);
    }

    @Transactional(readOnly = true)
    public Optional<Salary> getCurrentSalary(Long employeeId) {
        return salaryRepository.findFirstByEmployeeIdOrderByEffectiveDateDesc(employeeId);
    }

    @Transactional(readOnly = true)
    public Page<Salary> getSalaryHistory(Long employeeId, Pageable pageable) {
        return salaryRepository.findByEmployeeIdOrderByEffectiveDateDesc(employeeId, pageable);
    }
}
