package com.salary.controller;

import com.salary.domain.Salary;
import com.salary.dto.AddSalaryRequest;
import com.salary.dto.SalaryResponse;
import com.salary.service.SalaryService;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/employees/{employeeId}/salaries")
public class SalaryController {

    private final SalaryService salaryService;

    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    @GetMapping
    public Page<SalaryResponse> history(@PathVariable Long employeeId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return salaryService.getSalaryHistory(employeeId, pageable).map(SalaryResponse::from);
    }

    @GetMapping("/current")
    public ResponseEntity<SalaryResponse> current(@PathVariable Long employeeId) {
        Optional<Salary> current = salaryService.getCurrentSalary(employeeId);
        return current.map(salary -> ResponseEntity.ok(SalaryResponse.from(salary)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SalaryResponse> add(@PathVariable Long employeeId,
                                               @Valid @RequestBody AddSalaryRequest request) {
        Salary created = salaryService.addSalary(
                employeeId, request.getBaseSalary(), request.getCurrencyCode(),
                request.getAnnualBonus(), request.getBenefitsValue(), request.getEffectiveDate());
        return ResponseEntity.status(HttpStatus.CREATED).body(SalaryResponse.from(created));
    }
}
