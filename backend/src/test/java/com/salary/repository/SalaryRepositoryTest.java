package com.salary.repository;

import com.salary.domain.Country;
import com.salary.domain.Employee;
import com.salary.domain.Salary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SalaryRepositoryTest {

    @Autowired
    private SalaryRepository salaryRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CountryRepository countryRepository;

    private Employee alice;

    @BeforeEach
    void setUp() {
        Country india = countryRepository.save(
                new Country("India", "INR", BigDecimal.valueOf(300000), BigDecimal.valueOf(5000000)));
        alice = employeeRepository.save(new Employee(
                "EMP000001", "Alice", "Singh", "alice.singh@company.com",
                "+91-9000000000", "Engineering", "Engineer", india, LocalDate.of(2020, 1, 1)));
    }

    @Test
    void findsMostRecentSalaryForEmployee() {
        salaryRepository.save(new Salary(alice, BigDecimal.valueOf(50000), "INR",
                null, null, LocalDate.of(2020, 1, 1)));
        salaryRepository.save(new Salary(alice, BigDecimal.valueOf(60000), "INR",
                null, null, LocalDate.of(2021, 1, 1)));
        salaryRepository.save(new Salary(alice, BigDecimal.valueOf(70000), "INR",
                null, null, LocalDate.of(2022, 1, 1)));

        Optional<Salary> latest = salaryRepository.findFirstByEmployeeIdOrderByEffectiveDateDesc(alice.getId());

        assertThat(latest).isPresent();
        assertThat(latest.get().getBaseSalary()).isEqualByComparingTo("70000");
    }

    @Test
    void returnsFullSalaryHistoryNewestFirst() {
        salaryRepository.save(new Salary(alice, BigDecimal.valueOf(50000), "INR",
                null, null, LocalDate.of(2020, 1, 1)));
        salaryRepository.save(new Salary(alice, BigDecimal.valueOf(60000), "INR",
                null, null, LocalDate.of(2021, 1, 1)));

        Page<Salary> history = salaryRepository.findByEmployeeIdOrderByEffectiveDateDesc(
                alice.getId(), PageRequest.of(0, 10));

        assertThat(history.getContent()).hasSize(2);
        assertThat(history.getContent().get(0).getBaseSalary()).isEqualByComparingTo("60000");
        assertThat(history.getContent().get(1).getBaseSalary()).isEqualByComparingTo("50000");
    }

    @Test
    void findsOnlyMostRecentSalaryPerEmployeeAcrossMultipleEmployees() {
        Employee bob = employeeRepository.save(new Employee(
                "EMP000002", "Bob", "Khan", "bob.khan@company.com",
                "+91-9000000001", "Sales", "Rep", alice.getCountry(), LocalDate.of(2021, 1, 1)));

        salaryRepository.save(new Salary(alice, BigDecimal.valueOf(50000), "INR", null, null, LocalDate.of(2020, 1, 1)));
        salaryRepository.save(new Salary(alice, BigDecimal.valueOf(70000), "INR", null, null, LocalDate.of(2022, 1, 1)));
        salaryRepository.save(new Salary(bob, BigDecimal.valueOf(40000), "INR", null, null, LocalDate.of(2021, 1, 1)));

        List<Salary> current = salaryRepository.findCurrentSalaryPerEmployee();

        assertThat(current).hasSize(2);
        assertThat(current).extracting(Salary::getBaseSalary)
                .containsExactlyInAnyOrder(new BigDecimal("70000"), new BigDecimal("40000"));
    }

    @Test
    void calculatesTotalCompensationIncludingBonusAndBenefits() {
        Salary salary = new Salary(alice, BigDecimal.valueOf(50000), "INR",
                BigDecimal.valueOf(5000), BigDecimal.valueOf(2000), LocalDate.of(2020, 1, 1));

        assertThat(salary.getTotalCompensation()).isEqualByComparingTo("57000");
    }

    @Test
    void calculatesTotalCompensationWhenBonusAndBenefitsAreAbsent() {
        Salary salary = new Salary(alice, BigDecimal.valueOf(50000), "INR",
                null, null, LocalDate.of(2020, 1, 1));

        assertThat(salary.getTotalCompensation()).isEqualByComparingTo("50000");
    }
}
