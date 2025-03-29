package org.example.swift.exception;

import org.example.swift.MessageDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionControllerTest {

    @Mock
    private BindingResult bindingResult;
    @Mock
    private HttpInputMessage httpInputMessage;
    @Mock
    private MethodParameter methodParameter;
    private ExceptionController exceptionController;

    @BeforeEach
    void setUp() {
        exceptionController = new ExceptionController();
    }

    @Test
    void whenRecordNotFound_givenRecordNotFoundException_thenReturnMessageDTO() {
        var recordNotFoundException = new RecordNotFoundException("message");

        ResponseEntity<MessageDTO> messageDTOResponseEntity = exceptionController
                .recordNotFound(recordNotFoundException);

        assertThat(messageDTOResponseEntity.getBody().message()).isEqualTo(recordNotFoundException.getMessage());
        assertThat(messageDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void whenRecordAlreadyExists_givenRecordAlreadyExistsException_thenReturnMessageDTO() {
        var recordAlreadyExistsException = new RecordAlreadyExistsException("message");

        ResponseEntity<MessageDTO> messageDTOResponseEntity = exceptionController
                .recordAlreadyExists(recordAlreadyExistsException);

        assertThat(messageDTOResponseEntity.getBody().message()).isEqualTo(recordAlreadyExistsException.getMessage());
        assertThat(messageDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void whenMismatchedData_givenMismatchedDataException_thenReturnMessageDTO() {
        var mismatchedDataException = new MismatchedDataException("message");

        ResponseEntity<MessageDTO> messageDTOResponseEntity = exceptionController
                .mismatchedData(mismatchedDataException);

        assertThat(messageDTOResponseEntity.getBody().message()).isEqualTo(mismatchedDataException.getMessage());
        assertThat(messageDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenMethodArgumentNotValid_givenMethodArgumentNotValidException_thenReturnMapOfErrors() {
        var fieldError1 = new FieldError("object", "field1", "message");
        var fieldError2 = new FieldError("object", "field2", "message");
        List<ObjectError> fieldErrors = List.of(fieldError1, fieldError2);
        var methodArgumentNotValidException = new MethodArgumentNotValidException(methodParameter, bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(fieldErrors);

        ResponseEntity<Map<String, List<String>>> mapResponseEntity = exceptionController
                .methodArgumentNotValid(methodArgumentNotValidException);

        assertThat(mapResponseEntity.getBody().size()).isEqualTo(2);
        assertThat(mapResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void whenHttpMessageNotReadable_givenHttpMessageNotReadableException_thenReturnMessageDTO() {
        var httpMessageNotReadableException = new HttpMessageNotReadableException("message", httpInputMessage);

        ResponseEntity<MessageDTO> messageDTOResponseEntity = exceptionController
                .httpMessageNotReadable(httpMessageNotReadableException);

        assertThat(messageDTOResponseEntity.getBody().message()).isEqualTo("Malformed request body");
        assertThat(messageDTOResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}