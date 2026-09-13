package com.salary.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Entity
@Table(name = "countries")
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotBlank
    @Column(name = "currency_code", nullable = false, length = 3)
    private String currencyCode;

    @NotNull
    @Positive
    @Column(name = "salary_min", nullable = false)
    private BigDecimal salaryMin;

    @NotNull
    @Positive
    @Column(name = "salary_max", nullable = false)
    private BigDecimal salaryMax;

    protected Country() {
        // JPA requires a no-arg constructor
    }

    public Country(String name, String currencyCode, BigDecimal salaryMin, BigDecimal salaryMax) {
        this.name = name;
        this.currencyCode = currencyCode;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getSalaryMin() {
        return salaryMin;
    }

    public BigDecimal getSalaryMax() {
        return salaryMax;
    }
}
