package com.salary.exception;

public class CountryNotFoundException extends RuntimeException {

    public CountryNotFoundException(String countryName) {
        super("No country found with name " + countryName);
    }
}
