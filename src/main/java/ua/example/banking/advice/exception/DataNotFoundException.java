package ua.example.banking.advice.exception;

import org.springframework.http.HttpStatus;

public class DataNotFoundException extends BankingException {

    public DataNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}