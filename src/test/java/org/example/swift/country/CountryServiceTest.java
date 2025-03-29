package org.example.swift.country;

import org.example.swift.exception.RecordNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CountryServiceTest {

    @Mock
    private CountryRepository countryRepository;
    @Mock
    private CountryMapper countryMapper;
    @InjectMocks
    private CountryService countryService;

    @Test
    void whenGetCountry_givenExistingIso2Code_thenReturnCountryDTO() {
        String iso2Code = "ab";
        var country = new Country();
        country.setIso2Code("ab");
        country.setName("name");
        var givenCountryDTO = CountryDTO.builder()
                .countryName("name")
                .countryISO2("ab")
                .build();
        when(countryRepository.findCountryByIso2CodeIgnoreCase(anyString())).thenReturn(Optional.of(country));
        when(countryMapper.countryToCountryDTO(any(Country.class))).thenReturn(givenCountryDTO);

        CountryDTO countryDTO = countryService.getCountry(iso2Code);

        assertThat(countryDTO.countryISO2()).isEqualTo(iso2Code);
    }

    @Test
    void whenGetCountry_givenNotExistingIso2Code_thenThrowRecordNotFoundException() {
        String iso2Code = "ab";
        when(countryRepository.findCountryByIso2CodeIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> countryService.getCountry(iso2Code))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("Country with the given iso2 code not found");
    }
}