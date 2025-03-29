package org.example.swift.country;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findCountryByIso2CodeIgnoreCase(String iso2Code);
    Optional<Country> findCountryByIso2CodeOrNameIgnoreCase(String iso2Code, String name);
}
