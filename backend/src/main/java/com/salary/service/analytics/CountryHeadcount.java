package com.salary.service.analytics;

/** Headcount for one country. */
public final class CountryHeadcount {

    private final String countryName;
    private final long employeeCount;

    public CountryHeadcount(String countryName, long employeeCount) {
        this.countryName = countryName;
        this.employeeCount = employeeCount;
    }

    public String getCountryName() {
        return countryName;
    }

    public long getEmployeeCount() {
        return employeeCount;
    }
}
