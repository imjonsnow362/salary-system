package com.salary.repository;

import com.salary.domain.Country;
import com.salary.domain.Employee;
import com.salary.domain.EmploymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Country india;
    private Country usa;

    @BeforeEach
    void setUp() {
        india = countryRepository.save(
                new Country("India", "INR", BigDecimal.valueOf(300000), BigDecimal.valueOf(5000000)));
        usa = countryRepository.save(
                new Country("United States", "USD", BigDecimal.valueOf(30000), BigDecimal.valueOf(250000)));
    }

    @Test
    void findsEmployeeByEmployeeId() {
        Employee alice = employeeRepository.save(newEmployee("EMP000001", "Alice", "Singh", "Engineering", india));

        Optional<Employee> found = employeeRepository.findByEmployeeId("EMP000001");

        assertThat(found).isPresent();
        assertThat(found.get().getFullName()).isEqualTo("Alice Singh");
        assertThat(found.get().getId()).isEqualTo(alice.getId());
    }

    @Test
    void paginatesEmployeesByDepartment() {
        employeeRepository.save(newEmployee("EMP000001", "Alice", "Singh", "Engineering", india));
        employeeRepository.save(newEmployee("EMP000002", "Bob", "Khan", "Engineering", usa));
        employeeRepository.save(newEmployee("EMP000003", "Carol", "Lee", "Sales", india));

        Page<Employee> page = employeeRepository.findByDepartment("Engineering", PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent())
                .extracting(Employee::getFirstName)
                .containsExactlyInAnyOrder("Alice", "Bob");
    }

    @Test
    void countsEmployeesByCountry() {
        employeeRepository.save(newEmployee("EMP000001", "Alice", "Singh", "Engineering", india));
        employeeRepository.save(newEmployee("EMP000002", "Bob", "Khan", "Sales", india));
        employeeRepository.save(newEmployee("EMP000003", "Carol", "Lee", "Sales", usa));

        assertThat(employeeRepository.countByCountryId(india.getId())).isEqualTo(2);
        assertThat(employeeRepository.countByCountryId(usa.getId())).isEqualTo(1);
    }

    @Test
    void searchesEmployeesByNameCaseInsensitive() {
        employeeRepository.save(newEmployee("EMP000001", "Alice", "Singh", "Engineering", india));
        employeeRepository.save(newEmployee("EMP000002", "Bob", "Khan", "Sales", usa));

        Page<Employee> page = employeeRepository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        "ali", "ali", PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void defaultsNewEmployeeToActiveStatus() {
        Employee saved = employeeRepository.save(newEmployee("EMP000001", "Alice", "Singh", "Engineering", india));

        assertThat(saved.getEmploymentStatus()).isEqualTo(EmploymentStatus.ACTIVE);
    }

    private Employee newEmployee(String employeeId, String firstName, String lastName, String department, Country country) {
        return new Employee(
                employeeId, firstName, lastName,
                (firstName + "." + lastName + "@company.com").toLowerCase(),
                "+1-555-0100", department, "Engineer",
                country, LocalDate.of(2022, 1, 15));
    }
}
