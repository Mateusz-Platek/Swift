package org.example.swift.bank;

import org.example.swift.bank.dto.BankDTO;
import org.example.swift.bank.dto.BankListDTO;
import org.example.swift.bank.dto.CreateBankDTO;
import org.example.swift.country.Country;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankMapper {

    public BankListDTO bankToBankListDTO(Bank bank) {
        return BankListDTO.builder()
                .address(bank.getAddress())
                .bankName(bank.getName())
                .countryISO2(bank.getCountry().getIso2Code())
                .isHeadquarter(bank.isHeadquarter())
                .swiftCode(bank.getSwiftCode())
                .build();
    }

    public BankDTO bankToBankDTO(Bank branchBank) {
        return BankDTO.builder()
                .address(branchBank.getAddress())
                .bankName(branchBank.getName())
                .countryISO2(branchBank.getCountry().getIso2Code())
                .countryName(branchBank.getCountry().getName())
                .isHeadquarter(branchBank.isHeadquarter())
                .swiftCode(branchBank.getSwiftCode())
                .branches(new ArrayList<>())
                .build();
    }

    public BankDTO bankToBankDTO(Bank headquarterBank, List<BankListDTO> branchBanks) {
        return BankDTO.builder()
                .address(headquarterBank.getAddress())
                .bankName(headquarterBank.getName())
                .countryISO2(headquarterBank.getCountry().getIso2Code())
                .countryName(headquarterBank.getCountry().getName())
                .isHeadquarter(headquarterBank.isHeadquarter())
                .swiftCode(headquarterBank.getSwiftCode())
                .branches(branchBanks)
                .build();
    }

    public Bank createBankDTOToBank(CreateBankDTO createBankDTO, Country country) {
        String swiftCode = createBankDTO.swiftCode().toUpperCase();
        if (swiftCode.length() == 8) {
            swiftCode += "XXX";
        }

        var bank = new Bank();
        bank.setName(createBankDTO.bankName().toUpperCase());
        bank.setSwiftCode(swiftCode);
        if (createBankDTO.address() != null) {
            bank.setAddress(createBankDTO.address().toUpperCase());
        }
        bank.setHeadquarter(createBankDTO.isHeadquarter());
        bank.setCountry(country);

        return bank;
    }
}
