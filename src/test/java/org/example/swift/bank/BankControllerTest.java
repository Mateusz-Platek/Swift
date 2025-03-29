package org.example.swift.bank;

import org.example.swift.MessageDTO;
import org.example.swift.bank.dto.BankDTO;
import org.example.swift.bank.dto.CreateBankDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankControllerTest {

    @Mock
    private BankService bankService;
    @InjectMocks
    private BankController bankController;

    @Test
    void whenGetBank_givenSwiftCode_thenReturnBankDTO() {
        String swiftCode = "abcdabcd123";
        var bankDTO = BankDTO.builder()
                .bankName("name")
                .address("address")
                .swiftCode("abcdabcd123")
                .isHeadquarter(false)
                .countryISO2("ab")
                .countryName("abcd")
                .branches(new ArrayList<>())
                .build();
        when(bankService.getBank(swiftCode)).thenReturn(bankDTO);

        ResponseEntity<BankDTO> bankDTOResponseEntity = bankController.getBank(swiftCode);

        assertThat(bankDTOResponseEntity.getBody()).isEqualTo(bankDTO);
        assertThat(bankDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void whenCreateBank_givenCreateBankDTO_thenReturnMessageDTO() {
        var createBankDTO = CreateBankDTO.builder()
                .bankName("name")
                .address("address")
                .countryName("name")
                .countryISO2("ab")
                .swiftCode("abcdabcd123")
                .isHeadquarter(false)
                .build();
        var messageDTO = new MessageDTO("message");
        when(bankService.createBank(createBankDTO)).thenReturn(messageDTO);

        ResponseEntity<MessageDTO> messageDTOResponseEntity = bankController.createBank(createBankDTO);

        assertThat(messageDTOResponseEntity.getBody()).isEqualTo(messageDTO);
        assertThat(messageDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void whenDeleteBank_givenSwiftCode_thenReturnMessageDTO() {
        String swiftCode = "abcdabcd123";
        var messageDTO = new MessageDTO("message");
        when(bankService.deleteBank(swiftCode)).thenReturn(messageDTO);

        ResponseEntity<MessageDTO> messageDTOResponseEntity = bankController.deleteBank(swiftCode);

        assertThat(messageDTOResponseEntity.getBody()).isEqualTo(messageDTO);
        assertThat(messageDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}