package com.salary.controller;

import com.salary.service.AnalyticsService;
import com.salary.service.analytics.CountryHeadcount;
import com.salary.service.analytics.CountryPayrollCost;
import com.salary.service.analytics.DepartmentSalaryStat;
import com.salary.service.analytics.SalaryDistribution;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/salary-by-department")
    public List<DepartmentSalaryStat> salaryByDepartment() {
        return analyticsService.averageSalaryByDepartment();
    }

    @GetMapping("/headcount-by-country")
    public List<CountryHeadcount> headcountByCountry() {
        return analyticsService.headcountByCountry();
    }

    @GetMapping("/payroll-cost-by-country")
    public List<CountryPayrollCost> payrollCostByCountry() {
        return analyticsService.payrollCostByCountry();
    }

    @GetMapping("/salary-distribution-by-country")
    public List<SalaryDistribution> salaryDistributionByCountry() {
        return analyticsService.salaryDistributionByCountry();
    }
}
