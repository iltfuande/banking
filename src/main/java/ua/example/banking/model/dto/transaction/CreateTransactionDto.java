package ua.example.banking.model.dto.transaction;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.example.banking.model.enums.TransactionType;
import ua.example.banking.validation.annotation.AccountNumberValidation;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AccountNumberValidation
public class CreateTransactionDto {

    @NotNull(message = "Transaction type is required.")
    private TransactionType transactionType;

    private UUID from;

    private UUID to;

    @NotNull(message = "Amount is required.")
    @Digits(integer = 15, fraction = 2,
            message = "Amount must be a valid monetary value with up to 15 digits and 2 decimal places.")
    @Positive(message = "Transaction amount must be greater than zero.")
    private BigDecimal amount;
}
