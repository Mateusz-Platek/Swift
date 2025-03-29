package org.example.swift.country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.swift.MessageDTO;
import org.example.swift.bank.Bank;
import org.example.swift.bank.BankRepository;
import org.example.swift.bank.dto.BankListDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class CountryControllerIntegrationTest {

    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private BankRepository bankRepository;
    @Autowired
    private MockMvcTester mockMvcTester;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        var country = new Country();
        country.setName("GERMANY");
        country.setIso2Code("DE");
        var bank1 = new Bank();
        bank1.setName("NAME");
        bank1.setAddress("ADDRESS");
        bank1.setSwiftCode("ABCDABCDXXX");
        bank1.setHeadquarter(true);
        bank1.setCountry(country);
        var bank2 = new Bank();
        bank2.setName("NAME");
        bank2.setAddress("ADDRESS");
        bank2.setSwiftCode("ABCDABCD123");
        bank2.setHeadquarter(false);
        bank2.setCountry(country);
        bankRepository.saveAll(List.of(bank1, bank2));
    }

    @AfterEach
    void tearDown() {
        bankRepository.deleteAll();
        countryRepository.deleteAll();
    }

    @Test
    void whenGetCountry_givenExistingSwiftCode_thenReturnCountryDTO() throws JsonProcessingException {
        var bankListDTO1 = BankListDTO.builder()
                .bankName("NAME")
                .address("ADDRESS")
                .swiftCode("ABCDABCDXXX")
                .isHeadquarter(true)
                .countryISO2("DE")
                .build();
        var bankListDTO2 = BankListDTO.builder()
                .bankName("NAME")
                .address("ADDRESS")
                .swiftCode("ABCDABCD123")
                .isHeadquarter(false)
                .countryISO2("DE")
                .build();
        var countryDTO = CountryDTO.builder()
                .countryName("GERMANY")
                .countryISO2("DE")
                .swiftCodes(List.of(bankListDTO1, bankListDTO2))
                .build();

        MvcTestResult mvcTestResult = mockMvcTester.get().uri("/v1/swift-codes/country/de").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(countryDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.OK);
    }

    @Test
    void whenGetCountry_givenNotExistingSwiftCode_thenReturnMessageDTO() throws JsonProcessingException {
        var messageDTO = new MessageDTO("Country with the given iso2 code not found");

        MvcTestResult mvcTestResult = mockMvcTester.get().uri("/v1/swift-codes/country/ab").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.NOT_FOUND);
    }
}