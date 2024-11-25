package ua.example.banking.model.dto.transaction;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.example.banking.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {

    private Long id;
    private UUID accountNumberTo;
    private UUID accountNumberFrom;
    private BigDecimal amount;
    private TransactionType transactionType;
    private OffsetDateTime createDateTime;
}