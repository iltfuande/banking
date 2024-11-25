package ua.example.banking.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ua.example.banking.mappers.AccountMapper;
import ua.example.banking.model.dto.account.AccountDto;
import ua.example.banking.model.dto.account.CreateAccountDto;
import ua.example.banking.model.entity.Account;
import ua.example.banking.repository.AccountRepository;
import ua.example.banking.service.impl.AccountServiceImpl;
import ua.example.banking.util.DateUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    @DisplayName("Should create an account successfully with valid input")
    void shouldCreateAccountSuccessfully() {
        CreateAccountDto createAccountDto = new CreateAccountDto("user", BigDecimal.valueOf(1000));
        UUID accountNumber = UUID.randomUUID();
        Account mappedAccount = new Account(null, null, createAccountDto.getOwnerName(), createAccountDto.getBalance(), null, null);
        Account savedAccount = new Account(1L, accountNumber, createAccountDto.getOwnerName(), createAccountDto.getBalance(), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());
        AccountDto accountDto = new AccountDto(1L, accountNumber, createAccountDto.getOwnerName(), createAccountDto.getBalance(), DateUtils.nowUTC(), DateUtils.nowUTC());

        when(accountMapper.mapToEntity(createAccountDto)).thenReturn(mappedAccount);
        when(accountRepository.save(mappedAccount)).thenReturn(savedAccount);
        when(accountMapper.mapToDto(savedAccount)).thenReturn(accountDto);

        AccountDto result = accountService.createAccount(createAccountDto);

        assertNotNull(result, "Result should not be null");
        assertThat(result.getId()).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(result.getOwnerName()).isEqualTo(createAccountDto.getOwnerName());
        assertThat(result.getBalance()).isEqualTo(createAccountDto.getBalance());
        assertThat(result.getCreateDateTime()).isNotNull();
        assertThat(result.getUpdateDateTime()).isNotNull();

        verify(accountMapper, times(1)).mapToEntity(createAccountDto);
        verify(accountRepository, times(1)).save(mappedAccount);
        verify(accountMapper, times(1)).mapToDto(savedAccount);
    }

    @Test
    @DisplayName("Should retrieve account details successfully by account number")
    void shouldGetAccountDetailsSuccessfully() {
        UUID accountNumber = UUID.randomUUID();
        Account account = new Account(1L, accountNumber, "user", BigDecimal.valueOf(500), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());
        AccountDto accountDto = new AccountDto(1L, accountNumber, "user", BigDecimal.valueOf(500), DateUtils.nowUTC(), DateUtils.nowUTC());

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(accountMapper.mapToDto(account)).thenReturn(accountDto);

        AccountDto result = accountService.getAccountDetails(accountNumber);

        assertNotNull(result, "Result should not be null");
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAccountNumber()).isEqualTo(accountNumber);
        assertThat(result.getOwnerName()).isEqualTo("user");
        assertThat(result.getBalance()).isEqualTo(BigDecimal.valueOf(500));
        assertThat(result.getCreateDateTime()).isNotNull();
        assertThat(result.getUpdateDateTime()).isNotNull();

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verify(accountMapper, times(1)).mapToDto(account);
    }

    @Test
    @DisplayName("Should throw exception when account with given account number is not found")
    void shouldThrowExceptionWhenAccountNotFound() {
        UUID accountNumber = UUID.randomUUID();
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.getAccountDetails(accountNumber));

        assertThat(exception.getMessage()).isEqualTo("Account with account number: %s not found.".formatted(accountNumber));

        verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
        verifyNoInteractions(accountMapper);
    }

    @Test
    @DisplayName("Should retrieve paginated accounts successfully for multiple pages")
    void shouldRetrievePaginatedAccountsSuccessfully() {
        PageRequest firstPageRequest = PageRequest.of(0, 2);
        PageRequest secondPageRequest = PageRequest.of(1, 2);

        Account account1 = new Account(1L, UUID.randomUUID(), "User1", BigDecimal.valueOf(1000), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());
        Account account2 = new Account(2L, UUID.randomUUID(), "User2", BigDecimal.valueOf(2000), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());
        Account account3 = new Account(3L, UUID.randomUUID(), "User3", BigDecimal.valueOf(3000), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());

        Page<Account> firstAccountPage = new PageImpl<>(List.of(account1, account2), firstPageRequest, 3);
        AccountDto accountDto1 = new AccountDto(1L, account1.getAccountNumber(), account1.getOwnerName(), account1.getBalance(), DateUtils.timestampToOffsetDateTime(account1.getCreateDateTime()), DateUtils.timestampToOffsetDateTime(account1.getUpdateDateTime()));
        AccountDto accountDto2 = new AccountDto(2L, account2.getAccountNumber(), account2.getOwnerName(), account2.getBalance(), DateUtils.timestampToOffsetDateTime(account2.getCreateDateTime()), DateUtils.timestampToOffsetDateTime(account2.getUpdateDateTime()));

        Page<Account> secondAccountPage = new PageImpl<>(List.of(account3), secondPageRequest, 3);
        AccountDto accountDto3 = new AccountDto(3L, account3.getAccountNumber(), account3.getOwnerName(), account3.getBalance(), DateUtils.timestampToOffsetDateTime(account3.getCreateDateTime()), DateUtils.timestampToOffsetDateTime(account3.getUpdateDateTime()));

        when(accountRepository.findAll(firstPageRequest)).thenReturn(firstAccountPage);
        when(accountRepository.findAll(secondPageRequest)).thenReturn(secondAccountPage);
        when(accountMapper.mapToDto(account1)).thenReturn(accountDto1);
        when(accountMapper.mapToDto(account2)).thenReturn(accountDto2);
        when(accountMapper.mapToDto(account3)).thenReturn(accountDto3);

        Page<AccountDto> firstPageResult = accountService.getAccounts(firstPageRequest);
        Page<AccountDto> secondPageResult = accountService.getAccounts(secondPageRequest);

        assertNotNull(firstPageResult, "First page result should not be null");
        assertThat(firstPageResult.getContent()).hasSize(2);
        assertThat(firstPageResult.getContent()).containsExactly(accountDto1, accountDto2);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(3);
        assertThat(firstPageResult.getNumber()).isEqualTo(0);

        assertNotNull(secondPageResult, "Second page result should not be null");
        assertThat(secondPageResult.getContent()).hasSize(1);
        assertThat(secondPageResult.getContent()).containsExactly(accountDto3);
        assertThat(secondPageResult.getTotalElements()).isEqualTo(3);
        assertThat(secondPageResult.getNumber()).isEqualTo(1);

        verify(accountRepository, times(1)).findAll(firstPageRequest);
        verify(accountRepository, times(1)).findAll(secondPageRequest);
        verify(accountMapper, times(1)).mapToDto(account1);
        verify(accountMapper, times(1)).mapToDto(account2);
        verify(accountMapper, times(1)).mapToDto(account3);
    }

    @Test
    @DisplayName("Should retrieve an empty page when no accounts exist")
    void shouldRetrieveEmptyPageWhenNoAccountsExist() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Account> emptyPage = Page.empty();

        when(accountRepository.findAll(pageRequest)).thenReturn(emptyPage);

        Page<AccountDto> result = accountService.getAccounts(pageRequest);

        assertNotNull(result, "Result should not be null");
        assertThat(result.getContent()).isEmpty();

        verify(accountRepository, times(1)).findAll(pageRequest);
        verifyNoInteractions(accountMapper);
    }
}
