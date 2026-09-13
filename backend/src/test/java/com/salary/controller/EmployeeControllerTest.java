package com.salary.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.salary.domain.Country;
import com.salary.domain.Employee;
import com.salary.dto.CreateEmployeeRequest;
import com.salary.exception.CountryNotFoundException;
import com.salary.exception.EmployeeNotFoundException;
import com.salary.exception.GlobalExceptionHandler;
import com.salary.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@org.springframework.context.annotation.Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private Employee sampleEmployee() {
        Country india = new Country("India", "INR", BigDecimal.valueOf(300000), BigDecimal.valueOf(5000000));
        return new Employee("EMP000001", "Alice", "Singh", "alice.singh@company.com",
                "+91-9000000000", "Engineering", "Engineer", india, LocalDate.of(2022, 1, 1));
    }

    @Test
    void getEmployeeByIdReturns200WithBody() throws Exception {
        when(employeeService.getById(1L)).thenReturn(sampleEmployee());

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").value("Alice Singh"))
                .andExpect(jsonPath("$.employeeId").value("EMP000001"))
                .andExpect(jsonPath("$.countryName").value("India"));
    }

    @Test
    void getEmployeeByIdReturns404WhenNotFound() throws Exception {
        when(employeeService.getById(99L)).thenThrow(EmployeeNotFoundException.forId(99L));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void listsEmployeesPaginated() throws Exception {
        Page<Employee> page = new PageImpl<>(Collections.singletonList(sampleEmployee()), PageRequest.of(0, 500), 1);
        when(employeeService.listAll(any())).thenReturn(page);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("Alice Singh"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void searchesEmployeesByNameQueryParam() throws Exception {
        Page<Employee> page = new PageImpl<>(Collections.singletonList(sampleEmployee()));
        when(employeeService.searchByName(anyString(), any())).thenReturn(page);

        mockMvc.perform(get("/api/employees").param("q", "ali"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].fullName").value("Alice Singh"));
    }

    @Test
    void createsEmployeeReturns201() throws Exception {
        when(employeeService.createEmployee(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(sampleEmployee());

        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setEmployeeId("EMP000001");
        request.setFirstName("Alice");
        request.setLastName("Singh");
        request.setEmail("alice.singh@company.com");
        request.setDepartment("Engineering");
        request.setDesignation("Engineer");
        request.setCountryName("India");
        request.setHiredDate(LocalDate.of(2022, 1, 1));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Alice Singh"));
    }

    @Test
    void createEmployeeReturns400WhenRequiredFieldMissing() throws Exception {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setFirstName("Alice");
        // employeeId, lastName, email, department, designation, countryName, hiredDate all missing

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEmployeeReturns404WhenCountryUnknown() throws Exception {
        when(employeeService.createEmployee(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new CountryNotFoundException("Atlantis"));

        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setEmployeeId("EMP000001");
        request.setFirstName("Alice");
        request.setLastName("Singh");
        request.setEmail("alice.singh@company.com");
        request.setDepartment("Engineering");
        request.setDesignation("Engineer");
        request.setCountryName("Atlantis");
        request.setHiredDate(LocalDate.of(2022, 1, 1));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deactivateEmployeeReturns204() throws Exception {
        mockMvc.perform(post("/api/employees/1/deactivate"))
                .andExpect(status().isNoContent());
    }
}
