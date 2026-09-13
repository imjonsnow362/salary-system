package com.salary.repository;

import com.salary.domain.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CountryRepositoryTest {

    @Autowired
    private CountryRepository countryRepository;

    @Test
    void savesAndFindsCountryByName() {
        Country india = new Country("India", "INR", BigDecimal.valueOf(300000), BigDecimal.valueOf(5000000));

        countryRepository.save(india);

        Optional<Country> found = countryRepository.findByName("India");

        assertThat(found).isPresent();
        assertThat(found.get().getCurrencyCode()).isEqualTo("INR");
        assertThat(found.get().getSalaryMin()).isEqualByComparingTo("300000");
    }

    @Test
    void returnsEmptyWhenCountryNameNotFound() {
        Optional<Country> found = countryRepository.findByName("Atlantis");

        assertThat(found).isEmpty();
    }
}
