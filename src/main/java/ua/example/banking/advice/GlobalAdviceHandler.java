package ua.example.banking.advice;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.example.banking.advice.exception.BankingException;
import ua.example.banking.util.DateUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalAdviceHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BankingException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(BankingException exception) {
        ErrorResponse errorResponse = createErrorResponse(
                exception.getHttpStatus().value(),
                exception.getMessage(),
                Collections.emptyList()
        );

        return new ResponseEntity<>(errorResponse, exception.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        Throwable cause = ex.getCause();
        List<Map<String, String>> errors;

        if (cause instanceof InvalidFormatException invalidFormatException) {
            errors = handleInvalidFormatException(invalidFormatException);
        } else {
            errors = List.of(Map.of("message", "Failed to read the request payload."));
        }

        ErrorResponse errorResponse = createErrorResponse(status.value(), "Validation failed", errors);
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        List<Map<String, String>> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> Map.of(
                        "field", fieldError.getField(),
                        "error", fieldError.getDefaultMessage()
                ))
                .toList();

        List<Map<String, String>> objectErrors = ex.getBindingResult()
                .getGlobalErrors()
                .stream()
                .map(objectError -> Map.of(
                        "object", objectError.getObjectName(),
                        "error", objectError.getDefaultMessage()
                ))
                .toList();

        List<Map<String, String>> allErrors = new ArrayList<>();
        allErrors.addAll(fieldErrors);
        allErrors.addAll(objectErrors);

        String message = allErrors.isEmpty() ? "Validation failed" : "Validation contains errors.";
        ErrorResponse errorResponse = createErrorResponse(status.value(), message, allErrors);

        return ResponseEntity.status(status).body(errorResponse);
    }

    private ErrorResponse createErrorResponse(int status, String message, List<Map<String, String>> errors) {
        return ErrorResponse.builder()
                .issuedAt(DateUtils.nowUTC())
                .status(status)
                .message(message)
                .errors(errors)
                .build();
    }

    private List<Map<String, String>> handleInvalidFormatException(InvalidFormatException ex) {
        String fieldName = ex.getPath().stream()
                .map(JsonMappingException.Reference::getFieldName)
                .findFirst()
                .orElse("unknown");

        String allowedValues = "unknown";
        if (ex.getTargetType().isEnum()) {
            allowedValues = Arrays.toString(ex.getTargetType().getEnumConstants());
        }

        String errorMessage = String.format(
                "Invalid value '%s' for field '%s'. Allowed values are: %s",
                ex.getValue(),
                fieldName,
                allowedValues
        );

        return List.of(Map.of("field", fieldName, "message", errorMessage));
    }
}
