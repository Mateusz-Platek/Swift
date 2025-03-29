package org.example.swift.bank;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.swift.MessageDTO;
import org.example.swift.bank.dto.BankDTO;
import org.example.swift.bank.dto.CreateBankDTO;
import org.example.swift.exception.MismatchedDataException;
import org.example.swift.exception.RecordAlreadyExistsException;
import org.example.swift.exception.RecordNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@WebMvcTest(BankController.class)
class BankControllerWebTest {

    @Autowired
    private MockMvcTester mockMvcTester;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private BankService bankService;

    @Test
    void whenGetBank_givenExistingSwiftCode_thenReturnBankDTO() throws JsonProcessingException {
        var bankDTO = BankDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("abcdabcd123")
                .isHeadquarter(false)
                .countryISO2("ab")
                .countryName("name")
                .build();
        when(bankService.getBank(anyString())).thenReturn(bankDTO);

        MvcTestResult mvcTestResult = mockMvcTester.get().uri("/v1/swift-codes/abcdabcd123").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(bankDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.OK);
    }

    @Test
    void whenGetBank_givenNotExistingSwiftCode_thenReturnMessageDTO() throws JsonProcessingException {
        var messageDTO = new MessageDTO("message");
        when(bankService.getBank(anyString())).thenThrow(new RecordNotFoundException("message"));

        MvcTestResult mvcTestResult = mockMvcTester.get().uri("/v1/swift-codes/abcdabcd123").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenCreateBank_givenCorrectCreateBankDTO_thenReturnMessageDTO() throws JsonProcessingException {
        var createBankDTO = CreateBankDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("abcdabcd123")
                .isHeadquarter(false)
                .countryISO2("ab")
                .countryName("name")
                .build();
        var messageDTO = new MessageDTO("message");
        when(bankService.createBank(any(CreateBankDTO.class))).thenReturn(messageDTO);

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
        var messageDTO = new MessageDTO("message");
        when(bankService.createBank(any(CreateBankDTO.class))).thenThrow(new RecordAlreadyExistsException("message"));

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
                .swiftCode("abcdabcdxxx")
                .isHeadquarter(false)
                .countryISO2("ab")
                .countryName("name")
                .build();
        var messageDTO = new MessageDTO("message");
        when(bankService.createBank(any(CreateBankDTO.class))).thenThrow(new MismatchedDataException("message"));

        MvcTestResult mvcTestResult = mockMvcTester.post().uri("/v1/swift-codes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createBankDTO))
                .exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenCreateBank_givenIncorrectCreateBankDTO_thenReturnMessageDTO() throws JsonProcessingException {
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
        var messageDTO = new MessageDTO("message");
        when(bankService.deleteBank(anyString())).thenReturn(messageDTO);

        MvcTestResult mvcTestResult = mockMvcTester.delete().uri("/v1/swift-codes/abcdabcd123").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.OK);
    }

    @Test
    void whenCreateBank_givenNotExistingSwiftCode_thenReturnMessageDTO() throws JsonProcessingException {
        var messageDTO = new MessageDTO("message");
        when(bankService.deleteBank(anyString())).thenThrow(new RecordNotFoundException("message"));

        MvcTestResult mvcTestResult = mockMvcTester.delete().uri("/v1/swift-codes/abcdabcd123").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.NOT_FOUND);
    }
}