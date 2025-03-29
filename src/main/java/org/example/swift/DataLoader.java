package org.example.swift;

import lombok.AllArgsConstructor;
import org.example.swift.bank.Bank;
import org.example.swift.bank.BankRepository;
import org.example.swift.country.Country;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Component
@AllArgsConstructor
@Profile("!test")
public class DataLoader implements CommandLineRunner {

    private BankRepository bankRepository;

    @Override
    public void run(String... args) {
        var records = new ArrayList<List<String>>();
        try (var scanner = new Scanner(new File("./Interns_2025_SWIFT_CODES - Sheet1.csv"))) {
            while (scanner.hasNextLine()) {
                records.add(getRecordFromLine(scanner.nextLine()));
            }
        } catch (FileNotFoundException fileNotFoundException) {
            throw new RuntimeException(fileNotFoundException);
        }
        records.removeFirst();

        List<Bank> banks = mapRecordsToBanks(records);
        bankRepository.saveAll(banks);
    }

    private List<Bank> mapRecordsToBanks(List<List<String>> records) {
        var banks = new ArrayList<Bank>();
        var countries = new ArrayList<Country>();

        for (List<String> record : records) {
            String countryName = record.get(6).trim();
            String countryIso2Code = record.get(0).trim();

            Country country = new Country();
            country.setName(countryName);
            country.setIso2Code(countryIso2Code);

            int index = countries.indexOf(country);
            if (index == -1) {
                countries.add(country);
            } else {
                country = countries.get(index);
            }

            String swiftCode = record.get(1).trim();
            boolean isHeadquarter = swiftCode.endsWith("XXX");
            String address = record.get(4).replace("\"", "").trim();
            if (address.isEmpty()) {
                address = null;
            }
            String bankName = record.get(3).replace("\"", "").trim();

            Bank bank = new Bank();
            bank.setName(bankName);
            bank.setSwiftCode(swiftCode);
            bank.setAddress(address);
            bank.setHeadquarter(isHeadquarter);
            bank.setCountry(country);
            banks.add(bank);
        }

        return banks;
    }

    private List<String> getRecordFromLine(String input) {
        var tokens = new ArrayList<String>();
        int startPosition = 0;
        boolean isInQuotes = false;

        for (int currentPosition = 0; currentPosition < input.length(); currentPosition++) {
            if (input.charAt(currentPosition) == '\"') {
                isInQuotes = !isInQuotes;
            }
            else if (input.charAt(currentPosition) == ',' && !isInQuotes) {
                tokens.add(input.substring(startPosition, currentPosition));
                startPosition = currentPosition + 1;
            }
        }

        String lastToken = input.substring(startPosition);
        if (lastToken.equals(",")) {
            tokens.add("");
        } else {
            tokens.add(lastToken);
        }

        return tokens;
    }
}
