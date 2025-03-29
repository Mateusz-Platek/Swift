package org.example.swift.country;

import lombok.RequiredArgsConstructor;
import org.example.swift.bank.BankMapper;
import org.example.swift.bank.dto.BankListDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CountryMapper {

    private final BankMapper bankMapper;

    public CountryDTO countryToCountryDTO(Country country) {
        List<BankListDTO> bankListDTOs = country.getBanks().stream()
                .map(bankMapper::bankToBankListDTO)
                .toList();

        return CountryDTO.builder()
                .countryISO2(country.getIso2Code())
                .countryName(country.getName())
                .swiftCodes(bankListDTOs)
                .build();
    }
}
