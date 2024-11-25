package ua.example.banking.model.dto.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {

    private Long id;
    private UUID accountNumber;
    private String ownerName;
    private BigDecimal balance;
    private OffsetDateTime createDateTime;
    private OffsetDateTime updateDateTime;
}