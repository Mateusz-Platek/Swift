package org.example.swift.country;

import lombok.RequiredArgsConstructor;
import org.example.swift.exception.RecordNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository countryRepository;
    private final CountryMapper countryMapper;

    public CountryDTO getCountry(String iso2Code) {
        Optional<Country> countryOptional = countryRepository.findCountryByIso2CodeIgnoreCase(iso2Code);
        Country country = countryOptional
                .orElseThrow(() -> new RecordNotFoundException("Country with the given iso2 code not found"));

        return countryMapper.countryToCountryDTO(country);
    }
}
