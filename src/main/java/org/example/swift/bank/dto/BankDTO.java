package org.example.swift.bank.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
public record BankDTO(
        String address,
        String bankName,
        String countryISO2,
        String countryName,
        Boolean isHeadquarter,
        String swiftCode,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        List<BankListDTO> branches
) {
}
