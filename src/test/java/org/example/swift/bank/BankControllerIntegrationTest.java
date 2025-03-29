package org.example.swift.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.swift.MessageDTO;
import org.example.swift.bank.dto.BankDTO;
import org.example.swift.bank.dto.CreateBankDTO;
import org.example.swift.country.Country;
import org.example.swift.country.CountryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
class BankControllerIntegrationTest {

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
    void whenGetBank_givenExistingSwiftCode_thenReturnBankDTO() throws JsonProcessingException {
        var bankDTO = BankDTO.builder()
                .bankName("NAME")
                .address("ADDRESS")
                .swiftCode("ABCDABCD123")
                .isHeadquarter(false)
                .countryISO2("DE")
                .countryName("GERMANY")
                .build();

        MvcTestResult mvcTestResult = mockMvcTester.get().uri("/v1/swift-codes/abcdabcd123").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(bankDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.OK);
    }

    @Test
    void whenGetBank_givenNotExistingSwiftCode_thenReturnMessageDTO() throws JsonProcessingException {
        var messageDTO = new MessageDTO("Bank with the given swift code not found");

        MvcTestResult mvcTestResult = mockMvcTester.get().uri("/v1/swift-codes/zxcvzxcv123").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenCreateBank_givenCorrectCreateBankDTO_thenReturnMessageDTO() throws JsonProcessingException {
        var createBankDTO = CreateBankDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("zxcvzxcv123")
                .isHeadquarter(false)
                .countryISO2("fr")
                .countryName("france")
                .build();
        var messageDTO = new MessageDTO("Bank created");

        MvcTestResult mvcTestResult = mockMvcTester.post().uri("/v1/swift-codes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBankDTO)).exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.CREATED);
    }

    @Test
    void whenCreateBank_givenExistingCreateBankDTO_thenReturnMessageDTO() throws JsonProcessingException {
        var createBankDTO = CreateBankDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("abcdabcd123")
                .isHeadquarter(false)
                .countryISO2("ab")
                .countryName("name")
                .build();
        var messageDTO = new MessageDTO("Bank with the given swift code already exists");

        MvcTestResult mvcTestResult = mockMvcTester.post().uri("/v1/swift-codes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBankDTO))
                .exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.CONFLICT);
    }

    @Test
    void whenCreateBank_givenMismatchedCreateBankDTO_thenReturnMessageDTO() throws JsonProcessingException {
        var createBankDTO = CreateBankDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("zxcvzxcvxxx")
                .isHeadquarter(false)
                .countryISO2("ab")
                .countryName("name")
                .build();
        var messageDTO = new MessageDTO("isHeadquarter flag and swift code do not match");

        MvcTestResult mvcTestResult = mockMvcTester.post().uri("/v1/swift-codes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBankDTO))
                .exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenCreateBank_givenIncorrectCreateBankDTO_thenReturnMapOfErrors() throws JsonProcessingException {
        var createBankDTO = CreateBankDTO.builder()
                .bankName(null)
                .address("address")
                .swiftCode("abc")
                .isHeadquarter(null)
                .countryISO2("abdef")
                .countryName("name")
                .build();
        var errors = Map.of(
                "swiftCode", List.of("Incorrect swift code"),
                "bankName", List.of("Bank name is mandatory"),
                "countryISO2", List.of("Incorrect iso2 code"),
                "isHeadquarter", List.of("isHeadquarter flag is mandatory")
        );

        MvcTestResult mvcTestResult = mockMvcTester.post().uri("/v1/swift-codes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBankDTO))
                .exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(errors));
        mvcTestResult.assertThat().hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenCreateBank_givenMalformedCreateBankDTO_thenReturnMessageDTO() throws JsonProcessingException {
        var messageDTO = new MessageDTO("Malformed request body");

        MvcTestResult mvcTestResult = mockMvcTester.post().uri("/v1/swift-codes")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenCreateBank_givenExistingSwiftCode_thenReturnMessageDTO() throws JsonProcessingException {
        var messageDTO = new MessageDTO("Bank deleted successfully");

        MvcTestResult mvcTestResult = mockMvcTester.delete().uri("/v1/swift-codes/abcdabcd123").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.OK);
    }

    @Test
    void whenCreateBank_givenNotExistingSwiftCode_thenReturnMessageDTO() throws JsonProcessingException {
        var messageDTO = new MessageDTO("Bank with the given swift code not found");

        MvcTestResult mvcTestResult = mockMvcTester.delete().uri("/v1/swift-codes/zxcvzxcv123").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.NOT_FOUND);
    }
}