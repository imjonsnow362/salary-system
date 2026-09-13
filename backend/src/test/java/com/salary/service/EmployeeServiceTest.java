package com.salary.service;

import com.salary.domain.Country;
import com.salary.domain.Employee;
import com.salary.domain.EmploymentStatus;
import com.salary.exception.CountryNotFoundException;
import com.salary.exception.EmployeeNotFoundException;
import com.salary.repository.CountryRepository;
import com.salary.repository.EmployeeRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CountryRepository countryRepository;

    private EmployeeService employeeService;

    private Country india;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository, countryRepository);
        india = new Country("India", "INR", BigDecimal.valueOf(300000), BigDecimal.valueOf(5000000));
    }

    @Test
    void createsEmployeeWhenCountryExists() {
        when(countryRepository.findByName("India")).thenReturn(Optional.of(india));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee created = employeeService.createEmployee(
                "EMP000001", "Alice", "Singh", "alice.singh@company.com",
                "+91-9000000000", "Engineering", "Engineer", "India", LocalDate.of(2022, 1, 1));

        assertThat(created.getFullName()).isEqualTo("Alice Singh");
        assertThat(created.getCountry()).isEqualTo(india);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void throwsWhenCreatingEmployeeForUnknownCountry() {
        when(countryRepository.findByName("Atlantis")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.createEmployee(
                "EMP000001", "Alice", "Singh", "alice.singh@company.com",
                "+91-9000000000", "Engineering", "Engineer", "Atlantis", LocalDate.of(2022, 1, 1)))
                .isInstanceOf(CountryNotFoundException.class);
    }

    @Test
    void getsEmployeeById() {
        Employee alice = new Employee("EMP000001", "Alice", "Singh", "alice.singh@company.com",
                "+91-9000000000", "Engineering", "Engineer", india, LocalDate.of(2022, 1, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(alice));

        Employee found = employeeService.getById(1L);

        assertThat(found.getFullName()).isEqualTo("Alice Singh");
    }

    @Test
    void throwsWhenEmployeeIdNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getById(99L))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void deactivatesActiveEmployee() {
        Employee alice = new Employee("EMP000001", "Alice", "Singh", "alice.singh@company.com",
                "+91-9000000000", "Engineering", "Engineer", india, LocalDate.of(2022, 1, 1));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(alice));

        employeeService.deactivateEmployee(1L);

        assertThat(alice.getEmploymentStatus()).isEqualTo(EmploymentStatus.INACTIVE);
    }

    @Test
    void throwsWhenDeactivatingAlreadyInactiveEmployee() {
        Employee alice = new Employee("EMP000001", "Alice", "Singh", "alice.singh@company.com",
                "+91-9000000000", "Engineering", "Engineer", india, LocalDate.of(2022, 1, 1));
        alice.setEmploymentStatus(EmploymentStatus.INACTIVE);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(alice));

        assertThatThrownBy(() -> employeeService.deactivateEmployee(1L))
                .isInstanceOf(IllegalStateException.class);
    }
}
