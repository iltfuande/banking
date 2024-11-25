package ua.example.banking.advice.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BankingException {

    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
