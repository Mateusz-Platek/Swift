package org.example.swift.bank;

import org.example.swift.MessageDTO;
import org.example.swift.bank.dto.BankDTO;
import org.example.swift.bank.dto.BankListDTO;
import org.example.swift.bank.dto.CreateBankDTO;
import org.example.swift.country.Country;
import org.example.swift.country.CountryRepository;
import org.example.swift.exception.MismatchedDataException;
import org.example.swift.exception.RecordAlreadyExistsException;
import org.example.swift.exception.RecordNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private BankRepository bankRepository;
    @Mock
    private BankMapper bankMapper;
    @Mock
    private CountryRepository countryRepository;
    @InjectMocks
    private BankService bankService;

    @Test
    void whenGetBank_givenExistingHeadquarterBankSwiftCode_thenReturnBankDTO() {
        String swiftCode = "abcdabcdxxx";
        var country = new Country();
        country.setName("NAME");
        country.setIso2Code("AB");
        var headquarterBank = new Bank();
        headquarterBank.setName("NAME");
        headquarterBank.setAddress("ADDRESS");
        headquarterBank.setSwiftCode("ABCDABCDXXX");
        headquarterBank.setHeadquarter(true);
        headquarterBank.setCountry(country);
        var branchBank1 = new Bank();
        branchBank1.setName("NAME");
        branchBank1.setAddress("ADDRESS");
        branchBank1.setSwiftCode("ABCDABCD123");
        branchBank1.setHeadquarter(false);
        branchBank1.setCountry(country);
        var branchBank2 = new Bank();
        branchBank2.setName("NAME");
        branchBank2.setAddress("ADDRESS");
        branchBank2.setSwiftCode("ABCDABCD234");
        branchBank2.setHeadquarter(false);
        branchBank2.setCountry(country);
        var banks = List.of(headquarterBank, branchBank1, branchBank2);
        List<BankListDTO> branchBanks = Stream.of(branchBank1, branchBank2)
                .filter(bank -> !bank.getSwiftCode().endsWith("XXX"))
                .map(bankMapper::bankToBankListDTO)
                .toList();
        var givenBankDTO = BankDTO.builder()
                .bankName("NAME")
                .address("ADDRESS")
                .swiftCode("ABCDABCDXXX")
                .isHeadquarter(true)
                .countryISO2("AB")
                .countryName("NAME")
                .branches(branchBanks)
                .build();
        when(bankRepository.findBanksBySwiftCodeContainingIgnoreCase(anyString())).thenReturn(banks);
        when(bankMapper.bankToBankDTO(headquarterBank, branchBanks)).thenReturn(givenBankDTO);

        BankDTO bankDTO = bankService.getBank(swiftCode);

        assertThat(bankDTO.bankName()).isEqualTo(headquarterBank.getName());
        assertThat(bankDTO.address()).isEqualTo(headquarterBank.getAddress());
        assertThat(bankDTO.swiftCode()).isEqualTo(headquarterBank.getSwiftCode());
        assertThat(bankDTO.countryName()).isEqualTo(country.getName());
        assertThat(bankDTO.countryISO2()).isEqualTo(country.getIso2Code());
        assertThat(bankDTO.isHeadquarter()).isEqualTo(headquarterBank.isHeadquarter());
        assertThat(bankDTO.branches()).hasSize(2);
    }

    @Test
    void whenGetBank_givenNotExistingHeadquarterBankSwiftCode_thenReturnBankDTO() {
        String swiftCode = "abcdabcdxxx";
        when(bankRepository.findBanksBySwiftCodeContainingIgnoreCase(anyString())).thenReturn(List.of());

        assertThatThrownBy(() -> bankService.getBank(swiftCode))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("Bank with the given swift code not found");
    }

    @Test
    void whenGetBank_givenExistingBranchBankSwiftCode_thenReturnBankDTO() {
        String swiftCode = "abcdabcd123";
        var country = new Country();
        country.setName("NAME");
        country.setIso2Code("AB");
        var branchBank = new Bank();
        branchBank.setName("NAME");
        branchBank.setAddress("ADDRESS");
        branchBank.setSwiftCode("ABCDABCD123");
        branchBank.setHeadquarter(false);
        branchBank.setCountry(country);
        var givenBankDTO = BankDTO.builder()
                .bankName("NAME")
                .address("ADDRESS")
                .swiftCode("ABCDABCD123")
                .isHeadquarter(false)
                .countryISO2("AB")
                .countryName("NAME")
                .branches(new ArrayList<>())
                .build();
        when(bankRepository.findBankBySwiftCodeIgnoreCase(anyString())).thenReturn(Optional.of(branchBank));
        when(bankMapper.bankToBankDTO(branchBank)).thenReturn(givenBankDTO);

        BankDTO bankDTO = bankService.getBank(swiftCode);

        assertThat(bankDTO.bankName()).isEqualTo(branchBank.getName());
        assertThat(bankDTO.address()).isEqualTo(branchBank.getAddress());
        assertThat(bankDTO.swiftCode()).isEqualTo(branchBank.getSwiftCode());
        assertThat(bankDTO.countryName()).isEqualTo(country.getName());
        assertThat(bankDTO.countryISO2()).isEqualTo(country.getIso2Code());
        assertThat(bankDTO.isHeadquarter()).isEqualTo(branchBank.isHeadquarter());
        assertThat(bankDTO.branches()).hasSize(0);
    }

    @Test
    void whenGetBank_givenNotExistingBranchBankSwiftCode_thenThrowRecordNotFoundException() {
        String swiftCode = "abcdabcd123";
        when(bankRepository.findBankBySwiftCodeIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankService.getBank(swiftCode))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("Bank with the given swift code not found");
    }

    @Test
    void whenCreateBank_givenCorrectCreateBankDTO_thenReturnMessageDTO() {
        var createBankDTO = CreateBankDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("abcdabcdxxx")
                .isHeadquarter(true)
                .countryISO2("ab")
                .countryName("name")
                .build();
        var country = new Country();
        country.setName("NAME");
        country.setIso2Code("AB");
        var bank = new Bank();
        bank.setName("NAME");
        bank.setAddress("ADDRESS");
        bank.setHeadquarter(true);
        bank.setSwiftCode("ABCDABCDXXX");
        bank.setCountry(country);
        when(bankRepository.findBankBySwiftCodeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(bankMapper.createBankDTOToBank(any(CreateBankDTO.class), any(Country.class))).thenReturn(bank);

        MessageDTO messageDTO = bankService.createBank(createBankDTO);

        assertThat(messageDTO.message()).isEqualTo("Bank created");
    }

    @Test
    void whenCreateBank_givenCreateBankDTOWithExistingSwiftCode_thenThrowRecordAlreadyExistsException() {
        var createBankDTO = CreateBankDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("abcdabcdxxx")
                .isHeadquarter(true)
                .countryISO2("ab")
                .countryName("name")
                .build();
        var country = new Country();
        country.setName("NAME");
        country.setIso2Code("AB");
        var bank = new Bank();
        bank.setName("NAME");
        bank.setAddress("ADDRESS");
        bank.setHeadquarter(true);
        bank.setSwiftCode("ABCDABCDXXX");
        bank.setCountry(country);
        when(bankRepository.findBankBySwiftCodeIgnoreCase(anyString())).thenReturn(Optional.of(bank));

        assertThatThrownBy(() -> bankService.createBank(createBankDTO))
                .isInstanceOf(RecordAlreadyExistsException.class)
                .hasMessage("Bank with the given swift code already exists");
    }

    @Test
    void whenCreateBank_givenCreateBankDTOWithSwiftCodeAndIsHeadquarterMismatch_thenThrowIncorrectDataException() {
        var createBankDTO = CreateBankDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("abcdabcd123")
                .isHeadquarter(true)
                .countryISO2("ab")
                .countryName("name")
                .build();
        var country = new Country();
        country.setName("NAME");
        country.setIso2Code("AB");
        var bank = new Bank();
        bank.setName("NAME");
        bank.setAddress("ADDRESS");
        bank.setHeadquarter(true);
        bank.setSwiftCode("ABCDABCD123");
        bank.setCountry(country);
        when(bankRepository.findBankBySwiftCodeIgnoreCase(anyString())).thenReturn(Optional.empty());
        when(bankMapper.createBankDTOToBank(any(CreateBankDTO.class), any(Country.class))).thenReturn(bank);

        assertThatThrownBy(() -> bankService.createBank(createBankDTO))
                .isInstanceOf(MismatchedDataException.class)
                .hasMessage("isHeadquarter flag and swift code do not match");
    }

    @Test
    void whenDeleteBank_givenExistingSwiftCode_thenReturnMessageDTO() {
        String swiftCode = "abcdabcd123";
        when(bankRepository.deleteBankBySwiftCodeIgnoreCase(anyString())).thenReturn(1L);

        MessageDTO messageDTO = bankService.deleteBank(swiftCode);

        assertThat(messageDTO.message()).isEqualTo("Bank deleted successfully");
    }

    @Test
    void whenDeleteBank_givenNotExistingSwiftCode_thenThrowRecordNotFoundException() {
        String swiftCode = "abcdabcd123";
        when(bankRepository.deleteBankBySwiftCodeIgnoreCase(anyString())).thenReturn(0L);

        assertThatThrownBy(() -> bankService.deleteBank(swiftCode))
                .isInstanceOf(RecordNotFoundException.class)
                .hasMessage("Bank with the given swift code not found");
    }
}