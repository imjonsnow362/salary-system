package com.salary.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Immutable salary record. Salary changes are appended, never edited in place,
 * so the full compensation history is always auditable.
 */
@Entity
@Table(name = "salaries")
public class Salary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @Positive
    @Column(name = "base_salary", nullable = false)
    private BigDecimal baseSalary;

    @NotNull
    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @PositiveOrZero
    @Column(name = "annual_bonus")
    private BigDecimal annualBonus;

    @PositiveOrZero
    @Column(name = "benefits_value")
    private BigDecimal benefitsValue;

    @NotNull
    @Column(name = "effective_date", nullable = false)
    private LocalDate effectiveDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected Salary() {
        // JPA requires a no-arg constructor
    }

    public Salary(Employee employee, BigDecimal baseSalary, String currencyCode,
                  BigDecimal annualBonus, BigDecimal benefitsValue, LocalDate effectiveDate) {
        this.employee = employee;
        this.baseSalary = baseSalary;
        this.currencyCode = currencyCode;
        this.annualBonus = annualBonus;
        this.benefitsValue = benefitsValue;
        this.effectiveDate = effectiveDate;
    }

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getAnnualBonus() {
        return annualBonus;
    }

    public BigDecimal getBenefitsValue() {
        return benefitsValue;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public BigDecimal getTotalCompensation() {
        BigDecimal total = baseSalary;
        if (annualBonus != null) {
            total = total.add(annualBonus);
        }
        if (benefitsValue != null) {
            total = total.add(benefitsValue);
        }
        return total;
    }
}
