package com.salary.repository;

import com.salary.domain.Employee;
import com.salary.domain.EmploymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeId(String employeeId);

    Page<Employee> findByDepartment(String department, Pageable pageable);

    Page<Employee> findByCountryId(Long countryId, Pageable pageable);

    Page<Employee> findByEmploymentStatus(EmploymentStatus status, Pageable pageable);

    Page<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName, Pageable pageable);

    long countByCountryId(Long countryId);
}
