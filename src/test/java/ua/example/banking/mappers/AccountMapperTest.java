package ua.example.banking.mappers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ua.example.banking.model.dto.account.AccountDto;
import ua.example.banking.model.dto.account.CreateAccountDto;
import ua.example.banking.model.entity.Account;
import ua.example.banking.util.DateUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AccountMapperImpl.class)
class AccountMapperTest {

    @Autowired
    private AccountMapper accountMapper;

    @Test
    @DisplayName("Should map Account to AccountDto correctly")
    void shouldMapAccountToAccountDto() {
        UUID accountNumber = UUID.randomUUID();
        Timestamp createDateTime = DateUtils.nowTimestamp();
        Timestamp updateDateTime = DateUtils.nowTimestamp();

        Account account = new Account(1L, accountNumber, "User", BigDecimal.valueOf(2000.00), createDateTime, updateDateTime);

        AccountDto accountDto = accountMapper.mapToDto(account);

        assertThat(accountDto).isNotNull();
        assertThat(accountDto.getId()).isEqualTo(account.getId());
        assertThat(accountDto.getAccountNumber()).isEqualTo(account.getAccountNumber());
        assertThat(accountDto.getOwnerName()).isEqualTo(account.getOwnerName());
        assertThat(accountDto.getBalance()).isEqualTo(account.getBalance());
        assertThat(accountDto.getCreateDateTime()).isEqualTo(DateUtils.timestampToOffsetDateTime(account.getCreateDateTime()));
        assertThat(accountDto.getUpdateDateTime()).isEqualTo(DateUtils.timestampToOffsetDateTime(account.getUpdateDateTime()));
    }

    @Test
    @DisplayName("Should map CreateAccountDto to Account entity correctly")
    void shouldMapCreateAccountDtoToAccountEntity() {
        CreateAccountDto createAccountDto = new CreateAccountDto("User", BigDecimal.valueOf(2000.00));

        Account account = accountMapper.mapToEntity(createAccountDto);

        assertThat(account).isNotNull();
        assertThat(account.getId()).isNull();
        assertThat(account.getAccountNumber()).isNull();
        assertThat(account.getCreateDateTime()).isNull();
        assertThat(account.getUpdateDateTime()).isNull();
        assertThat(account.getOwnerName()).isEqualTo(createAccountDto.getOwnerName());
        assertThat(account.getBalance()).isEqualTo(createAccountDto.getBalance());
    }
}
