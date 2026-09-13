package com.salary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salary.domain.Country;
import com.salary.domain.Employee;
import com.salary.domain.Salary;
import com.salary.dto.AddSalaryRequest;
import com.salary.exception.EmployeeNotFoundException;
import com.salary.exception.GlobalExceptionHandler;
import com.salary.service.SalaryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SalaryController.class)
@Import(GlobalExceptionHandler.class)
class SalaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SalaryService salaryService;

    private Salary sampleSalary() {
        Country india = new Country("India", "INR", BigDecimal.valueOf(300000), BigDecimal.valueOf(5000000));
        Employee alice = new Employee("EMP000001", "Alice", "Singh", "alice.singh@company.com",
                "+91-9000000000", "Engineering", "Engineer", india, LocalDate.of(2020, 1, 1));
        return new Salary(alice, BigDecimal.valueOf(60000), "INR",
                BigDecimal.valueOf(5000), BigDecimal.valueOf(2000), LocalDate.of(2021, 1, 1));
    }

    @Test
    void getsCurrentSalaryReturns200() throws Exception {
        when(salaryService.getCurrentSalary(1L)).thenReturn(Optional.of(sampleSalary()));

        mockMvc.perform(get("/api/employees/1/salaries/current"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.baseSalary").value(60000))
                .andExpect(jsonPath("$.totalCompensation").value(67000));
    }

    @Test
    void getsCurrentSalaryReturns404WhenNoneExists() throws Exception {
        when(salaryService.getCurrentSalary(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employees/1/salaries/current"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getsSalaryHistoryPaginated() throws Exception {
        Page<Salary> page = new PageImpl<>(Collections.singletonList(sampleSalary()));
        when(salaryService.getSalaryHistory(eq(1L), any())).thenReturn(page);

        mockMvc.perform(get("/api/employees/1/salaries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].baseSalary").value(60000));
    }

    @Test
    void addsSalaryReturns201() throws Exception {
        when(salaryService.addSalary(eq(1L), any(), any(), any(), any(), any())).thenReturn(sampleSalary());

        AddSalaryRequest request = new AddSalaryRequest();
        request.setBaseSalary(BigDecimal.valueOf(60000));
        request.setCurrencyCode("INR");
        request.setAnnualBonus(BigDecimal.valueOf(5000));
        request.setBenefitsValue(BigDecimal.valueOf(2000));
        request.setEffectiveDate(LocalDate.of(2021, 1, 1));

        mockMvc.perform(post("/api/employees/1/salaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.baseSalary").value(60000));
    }

    @Test
    void addSalaryReturns400WhenBaseSalaryMissing() throws Exception {
        AddSalaryRequest request = new AddSalaryRequest();
        request.setCurrencyCode("INR");
        request.setEffectiveDate(LocalDate.of(2021, 1, 1));

        mockMvc.perform(post("/api/employees/1/salaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addSalaryReturns404WhenEmployeeUnknown() throws Exception {
        when(salaryService.addSalary(eq(99L), any(), any(), any(), any(), any()))
                .thenThrow(EmployeeNotFoundException.forId(99L));

        AddSalaryRequest request = new AddSalaryRequest();
        request.setBaseSalary(BigDecimal.valueOf(60000));
        request.setCurrencyCode("INR");
        request.setEffectiveDate(LocalDate.of(2021, 1, 1));

        mockMvc.perform(post("/api/employees/99/salaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
