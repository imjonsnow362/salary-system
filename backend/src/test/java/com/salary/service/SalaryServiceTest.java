package com.salary.service;

import com.salary.domain.Country;
import com.salary.domain.Employee;
import com.salary.domain.Salary;
import com.salary.exception.EmployeeNotFoundException;
import com.salary.repository.EmployeeRepository;
import com.salary.repository.SalaryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {

    @Mock
    private SalaryRepository salaryRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    private SalaryService salaryService;

    private Employee alice;

    @BeforeEach
    void setUp() {
        salaryService = new SalaryService(salaryRepository, employeeRepository);
        Country india = new Country("India", "INR", BigDecimal.valueOf(300000), BigDecimal.valueOf(5000000));
        alice = new Employee("EMP000001", "Alice", "Singh", "alice.singh@company.com",
                "+91-9000000000", "Engineering", "Engineer", india, LocalDate.of(2020, 1, 1));
    }

    @Test
    void addsSalaryWhenEffectiveDateIsOnOrAfterHireDate() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(salaryRepository.save(any(Salary.class))).thenAnswer(inv -> inv.getArgument(0));

        Salary salary = salaryService.addSalary(1L, BigDecimal.valueOf(60000), "INR",
                BigDecimal.valueOf(5000), BigDecimal.valueOf(2000), LocalDate.of(2021, 1, 1));

        assertThat(salary.getBaseSalary()).isEqualByComparingTo("60000");
        assertThat(salary.getEmployee()).isEqualTo(alice);
    }

    @Test
    void rejectsSalaryEffectiveBeforeHireDate() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(alice));

        assertThatThrownBy(() -> salaryService.addSalary(1L, BigDecimal.valueOf(60000), "INR",
                null, null, LocalDate.of(2019, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("hire date");
    }

    @Test
    void rejectsNonPositiveBaseSalary() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(alice));

        assertThatThrownBy(() -> salaryService.addSalary(1L, BigDecimal.ZERO, "INR",
                null, null, LocalDate.of(2021, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positive");
    }

    @Test
    void throwsWhenAddingSalaryForUnknownEmployee() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> salaryService.addSalary(99L, BigDecimal.valueOf(60000), "INR",
                null, null, LocalDate.of(2021, 1, 1)))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void getsCurrentSalary() {
        Salary latest = new Salary(alice, BigDecimal.valueOf(70000), "INR", null, null, LocalDate.of(2022, 1, 1));
        when(salaryRepository.findFirstByEmployeeIdOrderByEffectiveDateDesc(1L)).thenReturn(Optional.of(latest));

        Optional<Salary> current = salaryService.getCurrentSalary(1L);

        assertThat(current).isPresent();
        assertThat(current.get().getBaseSalary()).isEqualByComparingTo("70000");
    }
}
