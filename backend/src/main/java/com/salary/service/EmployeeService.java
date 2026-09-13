package com.salary.service;

import com.salary.domain.Country;
import com.salary.domain.Employee;
import com.salary.domain.EmploymentStatus;
import com.salary.exception.CountryNotFoundException;
import com.salary.exception.EmployeeNotFoundException;
import com.salary.repository.CountryRepository;
import com.salary.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CountryRepository countryRepository;

    public EmployeeService(EmployeeRepository employeeRepository, CountryRepository countryRepository) {
        this.employeeRepository = employeeRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional
    public Employee createEmployee(String employeeId, String firstName, String lastName, String email,
                                    String phoneNumber, String department, String designation,
                                    String countryName, LocalDate hiredDate) {
        Country country = countryRepository.findByName(countryName)
                .orElseThrow(() -> new CountryNotFoundException(countryName));

        Employee employee = new Employee(employeeId, firstName, lastName, email,
                phoneNumber, department, designation, country, hiredDate);

        return employeeRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public Employee getById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> EmployeeNotFoundException.forId(id));
    }

    @Transactional(readOnly = true)
    public Employee getByEmployeeId(String employeeId) {
        return employeeRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> EmployeeNotFoundException.forEmployeeId(employeeId));
    }

    @Transactional(readOnly = true)
    public Page<Employee> listAll(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Employee> searchByName(String query, Pageable pageable) {
        return employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                query, query, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Employee> listByDepartment(String department, Pageable pageable) {
        return employeeRepository.findByDepartment(department, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Employee> listByCountry(Long countryId, Pageable pageable) {
        return employeeRepository.findByCountryId(countryId, pageable);
    }

    @Transactional
    public void deactivateEmployee(Long id) {
        Employee employee = getById(id);
        if (employee.getEmploymentStatus() == EmploymentStatus.INACTIVE) {
            throw new IllegalStateException("Employee " + id + " is already inactive");
        }
        employee.setEmploymentStatus(EmploymentStatus.INACTIVE);
        employeeRepository.save(employee);
    }
}
