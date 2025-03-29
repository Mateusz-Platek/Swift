package org.example.swift.bank;

import lombok.RequiredArgsConstructor;
import org.example.swift.MessageDTO;
import org.example.swift.bank.dto.BankDTO;
import org.example.swift.bank.dto.BankListDTO;
import org.example.swift.bank.dto.CreateBankDTO;
import org.example.swift.country.Country;
import org.example.swift.country.CountryRepository;
import org.example.swift.exception.MismatchedDataException;
import org.example.swift.exception.RecordAlreadyExistsException;
import org.example.swift.exception.RecordNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankService {

    private final BankRepository bankRepository;
    private final CountryRepository countryRepository;
    private final BankMapper bankMapper;

    public BankDTO getBank(String swiftCode) {
        swiftCode = completeSwiftCode(swiftCode);

        if (swiftCode.endsWith("XXX")) {
            List<Bank> banks = bankRepository.findBanksBySwiftCodeContainingIgnoreCase(swiftCode.substring(0, 8));

            Optional<Bank> headquarterBankOptional = banks.stream()
                    .filter(bank -> bank.getSwiftCode().endsWith("XXX"))
                    .findFirst();
            Bank headquarterBank = headquarterBankOptional
                    .orElseThrow(() -> new RecordNotFoundException("Bank with the given swift code not found"));

            List<BankListDTO> branchBanks = banks.stream()
                    .filter(bank -> !bank.getSwiftCode().endsWith("XXX"))
                    .map(bankMapper::bankToBankListDTO)
                    .toList();

            return bankMapper.bankToBankDTO(headquarterBank, branchBanks);
        } else {
            Optional<Bank> optionalBank = bankRepository.findBankBySwiftCodeIgnoreCase(swiftCode);
            Bank bank = optionalBank
                    .orElseThrow(() -> new RecordNotFoundException("Bank with the given swift code not found"));

            return bankMapper.bankToBankDTO(bank);
        }
    }

    public MessageDTO createBank(CreateBankDTO createBankDTO) {
        String swiftCode = completeSwiftCode(createBankDTO.swiftCode());

        Optional<Bank> bankOptional = bankRepository.findBankBySwiftCodeIgnoreCase(swiftCode);
        if (bankOptional.isPresent()) {
            throw new RecordAlreadyExistsException("Bank with the given swift code already exists");
        }

        Country country = findOrCreateCountry(createBankDTO);
        Bank bank = bankMapper.createBankDTOToBank(createBankDTO, country);
        if (bank.isHeadquarter() && !bank.getSwiftCode().endsWith("XXX")) {
            throw new MismatchedDataException("isHeadquarter flag and swift code do not match");
        }
        if (!bank.isHeadquarter() && bank.getSwiftCode().endsWith("XXX")) {
            throw new MismatchedDataException("isHeadquarter flag and swift code do not match");
        }

        bankRepository.save(bank);

        return new MessageDTO("Bank created");
    }

    public MessageDTO deleteBank(String swiftCode) {
        long deletedRecord = bankRepository.deleteBankBySwiftCodeIgnoreCase(swiftCode);
        if (deletedRecord == 0) {
            throw new RecordNotFoundException("Bank with the given swift code not found");
        }

        return new MessageDTO("Bank deleted successfully");
    }

    private Country findOrCreateCountry(CreateBankDTO createBankDTO) {
        Optional<Country> countryOptional = countryRepository
                .findCountryByIso2CodeOrNameIgnoreCase(createBankDTO.countryISO2(), createBankDTO.countryName());

        countryOptional.ifPresent(country -> {
            if (!country.getIso2Code().equals(createBankDTO.countryISO2().toUpperCase()) ||
                    !country.getName().equals(createBankDTO.countryName().toUpperCase())) {
                throw new MismatchedDataException("Country name and iso2 code do not match");
            }
        });

        return countryOptional.orElseGet(() -> {
            var country = new Country();
            country.setName(createBankDTO.countryName().toUpperCase());
            country.setIso2Code(createBankDTO.countryISO2().toUpperCase());
            return country;
        });
    }

    private String completeSwiftCode(String swiftCode) {
        swiftCode = swiftCode.toUpperCase();
        if (swiftCode.length() == 8) {
            swiftCode += "XXX";
        }

        return swiftCode;
    }
}
