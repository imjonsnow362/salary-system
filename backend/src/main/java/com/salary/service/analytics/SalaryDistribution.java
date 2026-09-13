package com.salary.service.analytics;

import java.math.BigDecimal;

/**
 * Base-salary percentiles for one country, in that country's native currency.
 * Scoped per-country because salaries in different currencies (INR vs USD) aren't
 * directly comparable — mixing them into one global distribution would be misleading.
 */
public final class SalaryDistribution {

    private final String countryName;
    private final String currencyCode;
    private final BigDecimal p10;
    private final BigDecimal p50;
    private final BigDecimal p90;
    private final long sampleSize;

    public SalaryDistribution(String countryName, String currencyCode,
                               BigDecimal p10, BigDecimal p50, BigDecimal p90, long sampleSize) {
        this.countryName = countryName;
        this.currencyCode = currencyCode;
        this.p10 = p10;
        this.p50 = p50;
        this.p90 = p90;
        this.sampleSize = sampleSize;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getP10() {
        return p10;
    }

    public BigDecimal getP50() {
        return p50;
    }

    public BigDecimal getP90() {
        return p90;
    }

    public long getSampleSize() {
        return sampleSize;
    }
}
