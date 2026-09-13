package com.salary.controller;

import com.salary.domain.Employee;
import com.salary.dto.CreateEmployeeRequest;
import com.salary.dto.EmployeeResponse;
import com.salary.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private static final int DEFAULT_PAGE_SIZE = 500;

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public Page<EmployeeResponse> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Long countryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "" + DEFAULT_PAGE_SIZE) int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employees;
        if (q != null && !q.isEmpty()) {
            employees = employeeService.searchByName(q, pageable);
        } else if (department != null && !department.isEmpty()) {
            employees = employeeService.listByDepartment(department, pageable);
        } else if (countryId != null) {
            employees = employeeService.listByCountry(countryId, pageable);
        } else {
            employees = employeeService.listAll(pageable);
        }
        return employees.map(EmployeeResponse::from);
    }

    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable Long id) {
        return EmployeeResponse.from(employeeService.getById(id));
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request) {
        Employee created = employeeService.createEmployee(
                request.getEmployeeId(), request.getFirstName(), request.getLastName(), request.getEmail(),
                request.getPhoneNumber(), request.getDepartment(), request.getDesignation(),
                request.getCountryName(), request.getHiredDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(EmployeeResponse.from(created));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        employeeService.deactivateEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
