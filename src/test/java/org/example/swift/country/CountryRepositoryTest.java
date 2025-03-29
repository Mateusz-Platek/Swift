package org.example.swift.country;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CountryRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private CountryRepository countryRepository;

    @Test
    void whenFindCountryByIso2CodeIgnoreCase_givenExistingIso2Code_thenReturnCountryOptional() {
        String iso2Code = "ab";
        Country country = new Country();
        country.setIso2Code("ab");
        country.setName("name");
        testEntityManager.persist(country);

        Optional<Country> countryOptional = countryRepository.findCountryByIso2CodeIgnoreCase(iso2Code);

        assertThat(countryOptional).isNotEmpty();
        assertThat(countryOptional).hasValue(country);
    }

    @Test
    void whenFindCountryByIso2CodeOrNameIgnoreCase_givenExistingIso2CodeAndName_thenReturnCountryOptional() {
        String iso2Code = "ab";
        String name = "abcd";
        Country country = new Country();
        country.setIso2Code("ab");
        country.setName("name");
        testEntityManager.persist(country);

        Optional<Country> countryOptional = countryRepository.findCountryByIso2CodeOrNameIgnoreCase(iso2Code, name);

        assertThat(countryOptional).isNotEmpty();
        assertThat(countryOptional).hasValue(country);
    }
}