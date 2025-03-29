package org.example.swift.bank.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record CreateBankDTO(
        String address,
        @NotBlank(message = "Bank name is mandatory")
        String bankName,
        @NotBlank(message = "Country iso2 code is mandatory")
        @Pattern(regexp = "^[a-zA-Z]{2}$", message = "Incorrect iso2 code")
        String countryISO2,
        @NotBlank(message = "Country name is mandatory")
        String countryName,
        @NotNull(message = "isHeadquarter flag is mandatory")
        Boolean isHeadquarter,
        @NotBlank(message = "Swift code is mandatory")
        @Pattern(regexp = "^[a-zA-Z0-9]{8}$|^[a-zA-Z0-9]{11}$", message = "Incorrect swift code")
        String swiftCode
) {
}
