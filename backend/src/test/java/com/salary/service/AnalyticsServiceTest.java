package com.salary.service;

import com.salary.domain.Country;
import com.salary.domain.Employee;
import com.salary.domain.Salary;
import com.salary.repository.SalaryRepository;
import com.salary.service.analytics.CountryHeadcount;
import com.salary.service.analytics.CountryPayrollCost;
import com.salary.service.analytics.DepartmentSalaryStat;
import com.salary.service.analytics.SalaryDistribution;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private SalaryRepository salaryRepository;

    private AnalyticsService analyticsService;

    private Country india;
    private Country usa;

    private Employee employee(String id, String firstName, String department, Country country) {
        return new Employee(id, firstName, "Doe", firstName.toLowerCase() + "@company.com",
                "+1-555-0100", department, "Engineer", country, LocalDate.of(2020, 1, 1));
    }

    private List<Salary> sampleCurrentSalaries() {
        india = new Country("India", "INR", BigDecimal.valueOf(300000), BigDecimal.valueOf(5000000));
        usa = new Country("United States", "USD", BigDecimal.valueOf(30000), BigDecimal.valueOf(250000));

        Employee alice = employee("EMP1", "Alice", "Engineering", india);
        Employee bob = employee("EMP2", "Bob", "Engineering", india);
        Employee carol = employee("EMP3", "Carol", "Sales", usa);

        return Arrays.asList(
                new Salary(alice, BigDecimal.valueOf(500000), "INR", null, null, LocalDate.of(2023, 1, 1)),
                new Salary(bob, BigDecimal.valueOf(700000), "INR", null, null, LocalDate.of(2023, 1, 1)),
                new Salary(carol, BigDecimal.valueOf(100000), "USD", null, null, LocalDate.of(2023, 1, 1))
        );
    }

    @Test
    void computesAverageSalaryByDepartment() {
        when(salaryRepository.findCurrentSalaryPerEmployee()).thenReturn(sampleCurrentSalaries());
        analyticsService = new AnalyticsService(salaryRepository);

        List<DepartmentSalaryStat> stats = analyticsService.averageSalaryByDepartment();

        DepartmentSalaryStat engineering = stats.stream()
                .filter(s -> s.getDepartment().equals("Engineering"))
                .findFirst().orElseThrow(AssertionError::new);
        assertThat(engineering.getAverageSalary()).isEqualByComparingTo("600000");
        assertThat(engineering.getEmployeeCount()).isEqualTo(2);

        DepartmentSalaryStat sales = stats.stream()
                .filter(s -> s.getDepartment().equals("Sales"))
                .findFirst().orElseThrow(AssertionError::new);
        assertThat(sales.getAverageSalary()).isEqualByComparingTo("100000");
    }

    @Test
    void computesHeadcountByCountry() {
        when(salaryRepository.findCurrentSalaryPerEmployee()).thenReturn(sampleCurrentSalaries());
        analyticsService = new AnalyticsService(salaryRepository);

        List<CountryHeadcount> headcounts = analyticsService.headcountByCountry();

        Optional<CountryHeadcount> indiaCount = headcounts.stream()
                .filter(h -> h.getCountryName().equals("India")).findFirst();
        assertThat(indiaCount).isPresent();
        assertThat(indiaCount.get().getEmployeeCount()).isEqualTo(2);
    }

    @Test
    void computesPayrollCostPerCountryWithoutMixingCurrencies() {
        when(salaryRepository.findCurrentSalaryPerEmployee()).thenReturn(sampleCurrentSalaries());
        analyticsService = new AnalyticsService(salaryRepository);

        List<CountryPayrollCost> costs = analyticsService.payrollCostByCountry();

        CountryPayrollCost indiaCost = costs.stream()
                .filter(c -> c.getCountryName().equals("India")).findFirst().orElseThrow(AssertionError::new);
        assertThat(indiaCost.getCurrencyCode()).isEqualTo("INR");
        assertThat(indiaCost.getTotalCost()).isEqualByComparingTo("1200000");

        CountryPayrollCost usaCost = costs.stream()
                .filter(c -> c.getCountryName().equals("United States")).findFirst().orElseThrow(AssertionError::new);
        assertThat(usaCost.getCurrencyCode()).isEqualTo("USD");
        assertThat(usaCost.getTotalCost()).isEqualByComparingTo("100000");
    }

    @Test
    void computesSalaryDistributionPerCountry() {
        when(salaryRepository.findCurrentSalaryPerEmployee()).thenReturn(sampleCurrentSalaries());
        analyticsService = new AnalyticsService(salaryRepository);

        List<SalaryDistribution> distributions = analyticsService.salaryDistributionByCountry();

        SalaryDistribution india = distributions.stream()
                .filter(d -> d.getCountryName().equals("India")).findFirst().orElseThrow(AssertionError::new);
        assertThat(india.getSampleSize()).isEqualTo(2);
        assertThat(india.getP50()).isEqualByComparingTo("500000");
    }

    @Test
    void returnsEmptyListsWhenNoSalariesExist() {
        when(salaryRepository.findCurrentSalaryPerEmployee()).thenReturn(Arrays.asList());
        analyticsService = new AnalyticsService(salaryRepository);

        assertThat(analyticsService.averageSalaryByDepartment()).isEmpty();
        assertThat(analyticsService.headcountByCountry()).isEmpty();
        assertThat(analyticsService.payrollCostByCountry()).isEmpty();
        assertThat(analyticsService.salaryDistributionByCountry()).isEmpty();
    }
}
