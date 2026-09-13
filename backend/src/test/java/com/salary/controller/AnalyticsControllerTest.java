package com.salary.controller;

import com.salary.service.AnalyticsService;
import com.salary.service.analytics.CountryHeadcount;
import com.salary.service.analytics.CountryPayrollCost;
import com.salary.service.analytics.DepartmentSalaryStat;
import com.salary.service.analytics.SalaryDistribution;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
@Import(com.salary.exception.GlobalExceptionHandler.class)
class AnalyticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AnalyticsService analyticsService;

    @Test
    void salaryByDepartmentReturnsAggregatedList() throws Exception {
        when(analyticsService.averageSalaryByDepartment()).thenReturn(Collections.singletonList(
                new DepartmentSalaryStat("Engineering", BigDecimal.valueOf(600000), 2)));

        mockMvc.perform(get("/api/analytics/salary-by-department"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].department").value("Engineering"))
                .andExpect(jsonPath("$[0].averageSalary").value(600000))
                .andExpect(jsonPath("$[0].employeeCount").value(2));
    }

    @Test
    void headcountByCountryReturnsList() throws Exception {
        when(analyticsService.headcountByCountry()).thenReturn(Collections.singletonList(
                new CountryHeadcount("India", 2)));

        mockMvc.perform(get("/api/analytics/headcount-by-country"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].countryName").value("India"))
                .andExpect(jsonPath("$[0].employeeCount").value(2));
    }

    @Test
    void payrollCostByCountryReturnsList() throws Exception {
        when(analyticsService.payrollCostByCountry()).thenReturn(Collections.singletonList(
                new CountryPayrollCost("India", "INR", BigDecimal.valueOf(1200000))));

        mockMvc.perform(get("/api/analytics/payroll-cost-by-country"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currencyCode").value("INR"))
                .andExpect(jsonPath("$[0].totalCost").value(1200000));
    }

    @Test
    void salaryDistributionByCountryReturnsList() throws Exception {
        when(analyticsService.salaryDistributionByCountry()).thenReturn(Collections.singletonList(
                new SalaryDistribution("India", "INR",
                        BigDecimal.valueOf(500000), BigDecimal.valueOf(600000), BigDecimal.valueOf(700000), 2)));

        mockMvc.perform(get("/api/analytics/salary-distribution-by-country"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].p50").value(600000))
                .andExpect(jsonPath("$[0].sampleSize").value(2));
    }
}
