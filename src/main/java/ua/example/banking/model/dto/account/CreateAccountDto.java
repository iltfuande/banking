package ua.example.banking.model.dto.account;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountDto {

    @Size(max = 100)
    @NotBlank(message = "Owner name is required.")
    private String ownerName;

    @NotNull(message = "Balance is required.")
    @Digits(integer = 15, fraction = 2,
            message = "Balance must be a valid monetary value with up to 15 digits and 2 decimal places.")
    @PositiveOrZero(message = "Balance cannot be negative.")
    private BigDecimal balance;
}