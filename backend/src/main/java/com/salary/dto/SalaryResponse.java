package com.salary.dto;

import com.salary.domain.Salary;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class SalaryResponse {

    private final Long id;
    private final BigDecimal baseSalary;
    private final String currencyCode;
    private final BigDecimal annualBonus;
    private final BigDecimal benefitsValue;
    private final LocalDate effectiveDate;
    private final BigDecimal totalCompensation;

    private SalaryResponse(Long id, BigDecimal baseSalary, String currencyCode, BigDecimal annualBonus,
                            BigDecimal benefitsValue, LocalDate effectiveDate, BigDecimal totalCompensation) {
        this.id = id;
        this.baseSalary = baseSalary;
        this.currencyCode = currencyCode;
        this.annualBonus = annualBonus;
        this.benefitsValue = benefitsValue;
        this.effectiveDate = effectiveDate;
        this.totalCompensation = totalCompensation;
    }

    public static SalaryResponse from(Salary salary) {
        return new SalaryResponse(
                salary.getId(), salary.getBaseSalary(), salary.getCurrencyCode(),
                salary.getAnnualBonus(), salary.getBenefitsValue(), salary.getEffectiveDate(),
                salary.getTotalCompensation());
    }

    public Long getId() {
        return id;
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

    public BigDecimal getTotalCompensation() {
        return totalCompensation;
    }
}
