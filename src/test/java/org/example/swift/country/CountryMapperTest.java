package org.example.swift.country;

import org.example.swift.bank.Bank;
import org.example.swift.bank.BankMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CountryMapperTest {

    @Mock
    private BankMapper bankMapper;
    @InjectMocks
    private CountryMapper countryMapper;

    @Test
    void whenCountryToCountryDTO_givenCountry_thenReturnCountryDTO() {
        var bank1 = new Bank();
        bank1.setName("name");
        bank1.setAddress("address");
        bank1.setSwiftCode("abcdabcd123");
        bank1.setHeadquarter(false);
        var bank2 = new Bank();
        bank2.setName("name");
        bank2.setAddress("address");
        bank2.setSwiftCode("zxcvzxcv123");
        bank2.setHeadquarter(false);
        var banks = Set.of(bank1, bank2);
        var country = new Country();
        country.setIso2Code("ab");
        country.setName("name");
        country.setBanks(banks);

        CountryDTO countryDTO = countryMapper.countryToCountryDTO(country);

        assertThat(countryDTO.countryISO2()).isEqualTo(country.getIso2Code());
        assertThat(countryDTO.countryName()).isEqualTo(country.getName());
        assertThat(countryDTO.swiftCodes()).hasSize(2);
    }
}