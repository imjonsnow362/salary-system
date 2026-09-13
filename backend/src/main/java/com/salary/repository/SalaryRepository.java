package com.salary.repository;

import com.salary.domain.Salary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SalaryRepository extends JpaRepository<Salary, Long> {

    Page<Salary> findByEmployeeIdOrderByEffectiveDateDesc(Long employeeId, Pageable pageable);

    Optional<Salary> findFirstByEmployeeIdOrderByEffectiveDateDesc(Long employeeId);

    /**
     * One row per employee: their most recent salary record (by effective date).
     * Basis for every analytics aggregation (avg by department, distribution, payroll cost).
     */
    @Query("select s from Salary s where s.effectiveDate = " +
            "(select max(s2.effectiveDate) from Salary s2 where s2.employee.id = s.employee.id)")
    List<Salary> findCurrentSalaryPerEmployee();
}
