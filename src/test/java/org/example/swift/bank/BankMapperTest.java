package org.example.swift.bank;

import org.example.swift.bank.dto.BankDTO;
import org.example.swift.bank.dto.BankListDTO;
import org.example.swift.bank.dto.CreateBankDTO;
import org.example.swift.country.Country;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BankMapperTest {

    private BankMapper bankMapper;

    @BeforeEach
    void setUp() {
        bankMapper = new BankMapper();
    }

    @Test
    void whenBankToBankListDTO_givenBank_thenReturnBankListDTO() {
        Country country = new Country();
        country.setName("name");
        country.setIso2Code("ab");
        Bank bank = new Bank();
        bank.setName("name");
        bank.setAddress("address");
        bank.setSwiftCode("abcdabcd123");
        bank.setHeadquarter(false);
        bank.setCountry(country);

        BankListDTO bankListDTO = bankMapper.bankToBankListDTO(bank);

        assertThat(bankListDTO.bankName()).isEqualTo(bank.getName());
        assertThat(bankListDTO.address()).isEqualTo(bank.getAddress());
        assertThat(bankListDTO.swiftCode()).isEqualTo(bank.getSwiftCode());
        assertThat(bankListDTO.countryISO2()).isEqualTo(bank.getCountry().getIso2Code());
        assertThat(bankListDTO.isHeadquarter()).isEqualTo(bank.isHeadquarter());
    }

    @Test
    void whenBankToBankDTO_givenBank_thenReturnBankDTO() {
        Country country = new Country();
        country.setName("name");
        country.setIso2Code("ab");
        Bank bank = new Bank();
        bank.setName("name");
        bank.setAddress("address");
        bank.setSwiftCode("abcdabcd123");
        bank.setHeadquarter(false);
        bank.setCountry(country);

        BankDTO bankDTO = bankMapper.bankToBankDTO(bank);

        assertThat(bankDTO.bankName()).isEqualTo(bank.getName());
        assertThat(bankDTO.address()).isEqualTo(bank.getAddress());
        assertThat(bankDTO.swiftCode()).isEqualTo(bank.getSwiftCode());
        assertThat(bankDTO.countryName()).isEqualTo(bank.getCountry().getName());
        assertThat(bankDTO.countryISO2()).isEqualTo(bank.getCountry().getIso2Code());
        assertThat(bankDTO.isHeadquarter()).isEqualTo(bank.isHeadquarter());
        assertThat(bankDTO.branches()).isEmpty();
    }

    @Test
    void whenBankToBankDTO_givenBankAndBankListDTO_thenReturnBankDTO() {
        Country country = new Country();
        country.setName("name");
        country.setIso2Code("ab");
        Bank bank = new Bank();
        bank.setName("name");
        bank.setAddress("address");
        bank.setSwiftCode("abcdabcd123");
        bank.setHeadquarter(false);
        bank.setCountry(country);
        BankListDTO bankListDTO1 = BankListDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("abcdabcd123")
                .isHeadquarter(false)
                .countryISO2(country.getName())
                .build();
        BankListDTO bankListDTO2 = BankListDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("zxcvzxcv123")
                .isHeadquarter(false)
                .countryISO2(country.getName())
                .build();
        List<BankListDTO> bankListDTOs = List.of(bankListDTO1, bankListDTO2);

        BankDTO bankDTO = bankMapper.bankToBankDTO(bank, bankListDTOs);

        assertThat(bankDTO.bankName()).isEqualTo(bank.getName());
        assertThat(bankDTO.address()).isEqualTo(bank.getAddress());
        assertThat(bankDTO.swiftCode()).isEqualTo(bank.getSwiftCode());
        assertThat(bankDTO.countryName()).isEqualTo(bank.getCountry().getName());
        assertThat(bankDTO.countryISO2()).isEqualTo(bank.getCountry().getIso2Code());
        assertThat(bankDTO.isHeadquarter()).isEqualTo(bank.isHeadquarter());
        assertThat(bankDTO.branches()).hasSize(2);
    }

    @Test
    void whenCreateBankDTOToBank_givenCreateBankDTOAndCountry_thenReturnBank() {
        Country country = new Country();
        country.setName("NAME");
        country.setIso2Code("AB");
        CreateBankDTO createBankDTO = CreateBankDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("abcdabcd123")
                .countryName("name")
                .countryISO2("ab")
                .isHeadquarter(false)
                .build();

        Bank bank = bankMapper.createBankDTOToBank(createBankDTO, country);

        assertThat(bank.getName()).isEqualTo(createBankDTO.bankName().toUpperCase());
        assertThat(bank.getAddress()).isEqualTo(createBankDTO.address().toUpperCase());
        assertThat(bank.getSwiftCode()).isEqualTo(createBankDTO.swiftCode().toUpperCase());
        assertThat(bank.getCountry().getIso2Code()).isEqualTo(createBankDTO.countryISO2().toUpperCase());
        assertThat(bank.getCountry().getName()).isEqualTo(createBankDTO.countryName().toUpperCase());
        assertThat(bank.isHeadquarter()).isEqualTo(createBankDTO.isHeadquarter());
    }
}