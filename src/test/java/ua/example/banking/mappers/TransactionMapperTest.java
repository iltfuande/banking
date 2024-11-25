package ua.example.banking.mappers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.example.banking.model.dto.transaction.TransactionDto;
import ua.example.banking.model.entity.Transaction;
import ua.example.banking.model.enums.TransactionType;
import ua.example.banking.util.DateUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TransactionMapperImpl.class)
class TransactionMapperTest {

    @Autowired
    private TransactionMapper transactionMapper;

    @Test
    @DisplayName("Should map Transaction to TransactionDto correctly")
    void shouldMapTransactionToTransactionDto() {
        UUID accountNumberTo = UUID.randomUUID();
        UUID accountNumberFrom = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(1500.00);
        Timestamp createDateTime = DateUtils.nowTimestamp();

        Transaction transaction = new Transaction(1L, accountNumberTo, accountNumberFrom, amount, TransactionType.TRANSFER, createDateTime);

        TransactionDto transactionDto = transactionMapper.mapToDto(transaction);

        assertThat(transactionDto).isNotNull();
        assertThat(transactionDto.getId()).isEqualTo(transaction.getId());
        assertThat(transactionDto.getAccountNumberTo()).isEqualTo(transaction.getAccountNumberTo());
        assertThat(transactionDto.getAccountNumberFrom()).isEqualTo(transaction.getAccountNumberFrom());
        assertThat(transactionDto.getAmount()).isEqualTo(transaction.getAmount());
        assertThat(transactionDto.getTransactionType()).isEqualTo(transaction.getTransactionType());
        assertThat(transactionDto.getCreateDateTime()).isEqualTo(DateUtils.timestampToOffsetDateTime(transaction.getCreateDateTime()));
    }
}
