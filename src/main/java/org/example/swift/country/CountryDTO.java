package org.example.swift.country;

import lombok.Builder;
import org.example.swift.bank.dto.BankListDTO;

import java.util.List;

@Builder
public record CountryDTO(
        String countryISO2,
        String countryName,
        List<BankListDTO> swiftCodes
) {
}
