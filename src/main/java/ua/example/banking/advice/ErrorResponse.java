package ua.example.banking.advice;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ErrorResponse {

    private OffsetDateTime issuedAt;
    private int status;
    private String message;
    private List<Map<String, String>> errors;
}
