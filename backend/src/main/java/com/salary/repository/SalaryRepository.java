package com.salary.repository;

import com.salary.domain.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Long> {

    Page<Salary> findByEmployeeIdOrderByEffectiveDateDesc(Long employeeId, Pageable pageable);

    Optional<Salary> findFirstByEmployeeIdOrderByEffectiveDateDesc(Long employeeId);
}
