package org.example.swift.exception;

public class RecordAlreadyExistsException extends RuntimeException {

    public RecordAlreadyExistsException(String message) {
        super(message);
    }
}
