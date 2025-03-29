package org.example.swift.country;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.swift.MessageDTO;
import org.example.swift.exception.RecordNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.ArrayList;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;


@WebMvcTest(CountryController.class)
class CountryControllerWebTest {

    @Autowired
    private MockMvcTester mockMvcTester;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private CountryService countryService;

    @Test
    void whenGetCountry_givenExistingSwiftCode_thenReturnCountryDTO() throws JsonProcessingException {
        var countryDTO = CountryDTO.builder()
                .countryName("name")
                .countryISO2("ab")
                .swiftCodes(new ArrayList<>())
                .build();
        when(countryService.getCountry(anyString())).thenReturn(countryDTO);

        MvcTestResult mvcTestResult = mockMvcTester.get().uri("/v1/swift-codes/country/ab").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(countryDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.OK);
    }

    @Test
    void whenGetCountry_givenNotExistingSwiftCode_thenReturnMessageDTO() throws JsonProcessingException {
        var messageDTO = new MessageDTO("message");
        when(countryService.getCountry(anyString())).thenThrow(new RecordNotFoundException("message"));

        MvcTestResult mvcTestResult = mockMvcTester.get().uri("/v1/swift-codes/country/ab").exchange();

        mvcTestResult.assertThat().bodyJson().isEqualTo(objectMapper.writeValueAsString(messageDTO));
        mvcTestResult.assertThat().hasStatus(HttpStatus.NOT_FOUND);
    }
}