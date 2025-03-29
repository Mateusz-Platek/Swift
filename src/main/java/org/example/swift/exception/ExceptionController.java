package org.example.swift.exception;

import org.example.swift.MessageDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(RecordNotFoundException.class)
    public ResponseEntity<MessageDTO> recordNotFound(RecordNotFoundException recordNotFoundException) {
        var messageDTO = new MessageDTO(recordNotFoundException.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messageDTO);
    }

    @ExceptionHandler(RecordAlreadyExistsException.class)
    public ResponseEntity<MessageDTO> recordAlreadyExists(RecordAlreadyExistsException recordAlreadyExistsException) {
        var messageDTO = new MessageDTO(recordAlreadyExistsException.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(messageDTO);
    }

    @ExceptionHandler(MismatchedDataException.class)
    public ResponseEntity<MessageDTO> mismatchedData(MismatchedDataException mismatchedDataException) {
        var messageDTO = new MessageDTO(mismatchedDataException.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDTO);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> methodArgumentNotValid(MethodArgumentNotValidException methodArgumentNotValidException) {
        Map<String, List<String>> errors = methodArgumentNotValidException.getBindingResult().getAllErrors().stream()
                .map(error -> (FieldError) error)
                .collect(
                        groupingBy(
                                FieldError::getField,
                                mapping(FieldError::getDefaultMessage, toList())
                        )
                );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<MessageDTO> httpMessageNotReadable(HttpMessageNotReadableException httpMessageNotReadableException) {
        var messageDTO = new MessageDTO("Malformed request body");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageDTO);
    }
}
