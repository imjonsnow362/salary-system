package com.salary.service;

import com.salary.domain.Salary;
import com.salary.repository.SalaryRepository;
import com.salary.service.analytics.CountryHeadcount;
import com.salary.service.analytics.CountryPayrollCost;
import com.salary.service.analytics.DepartmentSalaryStat;
import com.salary.service.analytics.SalaryDistribution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * All aggregations are computed server-side over the "current salary per employee" snapshot
 * (~10k rows), never over the full salary history, and never sent to the client as raw rows.
 */
@Service
public class AnalyticsService {

    private final SalaryRepository salaryRepository;

    public AnalyticsService(SalaryRepository salaryRepository) {
        this.salaryRepository = salaryRepository;
    }

    @Transactional(readOnly = true)
    public List<DepartmentSalaryStat> averageSalaryByDepartment() {
        Map<String, List<Salary>> byDepartment = currentSalaries().stream()
                .collect(Collectors.groupingBy(s -> s.getEmployee().getDepartment()));

        List<DepartmentSalaryStat> result = new ArrayList<>();
        for (Map.Entry<String, List<Salary>> entry : byDepartment.entrySet()) {
            BigDecimal average = average(entry.getValue());
            result.add(new DepartmentSalaryStat(entry.getKey(), average, entry.getValue().size()));
        }
        result.sort(Comparator.comparing(DepartmentSalaryStat::getDepartment));
        return result;
    }

    @Transactional(readOnly = true)
    public List<CountryHeadcount> headcountByCountry() {
        Map<String, List<Salary>> byCountry = currentSalaries().stream()
                .collect(Collectors.groupingBy(s -> s.getEmployee().getCountry().getName()));

        List<CountryHeadcount> result = new ArrayList<>();
        for (Map.Entry<String, List<Salary>> entry : byCountry.entrySet()) {
            result.add(new CountryHeadcount(entry.getKey(), entry.getValue().size()));
        }
        result.sort(Comparator.comparing(CountryHeadcount::getCountryName));
        return result;
    }

    @Transactional(readOnly = true)
    public List<CountryPayrollCost> payrollCostByCountry() {
        Map<String, List<Salary>> byCountry = currentSalaries().stream()
                .collect(Collectors.groupingBy(s -> s.getEmployee().getCountry().getName()));

        List<CountryPayrollCost> result = new ArrayList<>();
        for (Map.Entry<String, List<Salary>> entry : byCountry.entrySet()) {
            List<Salary> salaries = entry.getValue();
            BigDecimal total = salaries.stream()
                    .map(Salary::getTotalCompensation)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            String currencyCode = salaries.get(0).getCurrencyCode();
            result.add(new CountryPayrollCost(entry.getKey(), currencyCode, total));
        }
        result.sort(Comparator.comparing(CountryPayrollCost::getCountryName));
        return result;
    }

    @Transactional(readOnly = true)
    public List<SalaryDistribution> salaryDistributionByCountry() {
        Map<String, List<Salary>> byCountry = currentSalaries().stream()
                .collect(Collectors.groupingBy(s -> s.getEmployee().getCountry().getName()));

        List<SalaryDistribution> result = new ArrayList<>();
        for (Map.Entry<String, List<Salary>> entry : byCountry.entrySet()) {
            List<BigDecimal> sorted = entry.getValue().stream()
                    .map(Salary::getBaseSalary)
                    .sorted()
                    .collect(Collectors.toList());
            String currencyCode = entry.getValue().get(0).getCurrencyCode();
            result.add(new SalaryDistribution(
                    entry.getKey(), currencyCode,
                    percentile(sorted, 10), percentile(sorted, 50), percentile(sorted, 90),
                    sorted.size()));
        }
        result.sort(Comparator.comparing(SalaryDistribution::getCountryName));
        return result;
    }

    private List<Salary> currentSalaries() {
        return salaryRepository.findCurrentSalaryPerEmployee();
    }

    private BigDecimal average(List<Salary> salaries) {
        BigDecimal sum = salaries.stream().map(Salary::getBaseSalary).reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(salaries.size()), MathContext.DECIMAL64);
    }

    /** Nearest-rank percentile over an already-sorted list. */
    private BigDecimal percentile(List<BigDecimal> sortedValues, int percentile) {
        if (sortedValues.isEmpty()) {
            return BigDecimal.ZERO;
        }
        int rank = (int) Math.ceil(percentile / 100.0 * sortedValues.size());
        int index = Math.max(0, Math.min(sortedValues.size() - 1, rank - 1));
        return sortedValues.get(index);
    }
}
