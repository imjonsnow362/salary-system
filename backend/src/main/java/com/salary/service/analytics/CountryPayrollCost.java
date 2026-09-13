package com.salary.service.analytics;

import java.math.BigDecimal;

/** Total current payroll cost for one country, in that country's native currency (currencies are never summed across countries). */
public final class CountryPayrollCost {

    private final String countryName;
    private final String currencyCode;
    private final BigDecimal totalCost;

    public CountryPayrollCost(String countryName, String currencyCode, BigDecimal totalCost) {
        this.countryName = countryName;
        this.currencyCode = currencyCode;
        this.totalCost = totalCost;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }
}
