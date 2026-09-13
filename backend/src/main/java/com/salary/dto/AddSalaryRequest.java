package com.salary.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class AddSalaryRequest {

    @NotNull
    @Positive
    private BigDecimal baseSalary;

    @NotBlank
    private String currencyCode;

    @PositiveOrZero
    private BigDecimal annualBonus;

    @PositiveOrZero
    private BigDecimal benefitsValue;

    @NotNull
    private LocalDate effectiveDate;

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getAnnualBonus() {
        return annualBonus;
    }

    public void setAnnualBonus(BigDecimal annualBonus) {
        this.annualBonus = annualBonus;
    }

    public BigDecimal getBenefitsValue() {
        return benefitsValue;
    }

    public void setBenefitsValue(BigDecimal benefitsValue) {
        this.benefitsValue = benefitsValue;
    }

    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
}
