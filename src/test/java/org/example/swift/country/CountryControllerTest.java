package org.example.swift.country;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CountryControllerTest {

    @Mock
    private CountryService countryService;
    @InjectMocks
    private CountryController countryController;

    @Test
    void whenGetCountry_givenIso2Code_thenReturnCountryDTO() {
        String iso2Code = "pl";
        var countryDTO = CountryDTO.builder()
                .countryISO2("ab")
                .countryName("abcd")
                .build();
        when(countryService.getCountry(anyString())).thenReturn(countryDTO);

        ResponseEntity<CountryDTO> countryDTOResponseEntity = countryController.getCountry(iso2Code);

        assertThat(countryDTOResponseEntity.getBody()).isEqualTo(countryDTO);
        assertThat(countryDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}