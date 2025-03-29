package org.example.swift.bank.dto;

import lombok.Builder;

@Builder
public record BankListDTO(
        String address,
        String bankName,
        String countryISO2,
        Boolean isHeadquarter,
        String swiftCode
) {
}
