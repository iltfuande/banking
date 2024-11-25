package ua.example.banking.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.example.banking.advice.exception.BankingException;
import ua.example.banking.advice.exception.DataNotFoundException;
import ua.example.banking.mappers.TransactionMapper;
import ua.example.banking.model.dto.transaction.CreateTransactionDto;
import ua.example.banking.model.dto.transaction.TransactionDto;
import ua.example.banking.model.entity.Account;
import ua.example.banking.model.entity.Transaction;
import ua.example.banking.model.enums.TransactionType;
import ua.example.banking.repository.AccountRepository;
import ua.example.banking.repository.TransactionRepository;
import ua.example.banking.service.impl.TransactionServiceImpl;
import ua.example.banking.util.DateUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Nested
    @DisplayName("Deposit Transaction Tests")
    class DepositTransactionTests {

        @Test
        @DisplayName("Should successfully deposit funds into an account")
        void shouldDepositFundsSuccessfully() {
            UUID accountNumber = UUID.randomUUID();
            BigDecimal amount = BigDecimal.valueOf(1000);
            CreateTransactionDto createTransactionDto = new CreateTransactionDto(TransactionType.DEPOSIT, null, accountNumber, amount);

            Account account = new Account(1L, accountNumber, "User1", BigDecimal.valueOf(5000), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());
            Account updatedAccount = new Account(1L, accountNumber, "User1", BigDecimal.valueOf(6000), account.getCreateDateTime(), DateUtils.nowTimestamp());

            Transaction transaction = new Transaction(accountNumber, null, amount, TransactionType.DEPOSIT, DateUtils.nowTimestamp());
            TransactionDto transactionDto = new TransactionDto(1L, accountNumber, null, amount, TransactionType.DEPOSIT, DateUtils.nowUTC());

            when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
            when(accountRepository.save(account)).thenReturn(updatedAccount);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
            when(transactionMapper.mapToDto(transaction)).thenReturn(transactionDto);

            TransactionDto result = transactionService.createTransaction(createTransactionDto);

            assertNotNull(result, "Result should not be null");
            assertThat(result.getAccountNumberTo()).isEqualTo(accountNumber);
            assertThat(result.getAmount()).isEqualTo(amount);
            assertThat(result.getTransactionType()).isEqualTo(TransactionType.DEPOSIT);

            verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
            verify(accountRepository, times(1)).save(account);
            verify(transactionRepository, times(1)).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Should throw exception when account for deposit is not found")
        void shouldThrowExceptionWhenAccountNotFoundForDeposit() {
            UUID accountNumber = UUID.randomUUID();
            BigDecimal amount = BigDecimal.valueOf(1000);
            CreateTransactionDto createTransactionDto = new CreateTransactionDto(TransactionType.DEPOSIT, null, accountNumber, amount);

            when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

            DataNotFoundException exception = assertThrows(DataNotFoundException.class, () ->
                    transactionService.createTransaction(createTransactionDto)
            );

            assertThat(exception.getMessage()).isEqualTo("Target account not found.");

            verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
            verifyNoInteractions(transactionRepository);
            verifyNoInteractions(transactionMapper);
        }
    }

    @Nested
    @DisplayName("Withdraw Transaction Tests")
    class WithdrawTransactionTests {

        @Test
        @DisplayName("Should successfully withdraw funds from an account")
        void shouldWithdrawFundsSuccessfully() {
            UUID accountNumber = UUID.randomUUID();
            BigDecimal amount = BigDecimal.valueOf(1000);
            CreateTransactionDto createTransactionDto = new CreateTransactionDto(TransactionType.WITHDRAW, accountNumber, null, amount);

            Account account = new Account(1L, accountNumber, "User1", BigDecimal.valueOf(5000), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());
            Account updatedAccount = new Account(1L, accountNumber, "User1", BigDecimal.valueOf(4000), account.getCreateDateTime(), DateUtils.nowTimestamp());

            Transaction transaction = new Transaction(null, accountNumber, amount, TransactionType.WITHDRAW, DateUtils.nowTimestamp());
            TransactionDto transactionDto = new TransactionDto(1L, null, accountNumber, amount, TransactionType.WITHDRAW, DateUtils.nowUTC());

            when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
            when(accountRepository.save(account)).thenReturn(updatedAccount);
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
            when(transactionMapper.mapToDto(transaction)).thenReturn(transactionDto);

            TransactionDto result = transactionService.createTransaction(createTransactionDto);

            assertNotNull(result, "Result should not be null");
            assertThat(result.getAccountNumberFrom()).isEqualTo(accountNumber);
            assertThat(result.getAmount()).isEqualTo(amount);
            assertThat(result.getTransactionType()).isEqualTo(TransactionType.WITHDRAW);

            verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
            verify(accountRepository, times(1)).save(account);
            verify(transactionRepository, times(1)).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Should throw exception when withdrawing more than account balance")
        void shouldThrowExceptionWhenInsufficientFunds() {
            UUID accountNumber = UUID.randomUUID();
            BigDecimal amount = BigDecimal.valueOf(10000);
            CreateTransactionDto createTransactionDto = new CreateTransactionDto(TransactionType.WITHDRAW, accountNumber, null, amount);

            Account account = new Account(1L, accountNumber, "User1", BigDecimal.valueOf(5000), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());

            when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionService.createTransaction(createTransactionDto));

            assertThat(exception.getMessage()).isEqualTo("Insufficient funds.");

            verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
            verifyNoInteractions(transactionRepository);
        }

        @Test
        @DisplayName("Should throw exception when account for withdrawal is not found")
        void shouldThrowExceptionWhenAccountNotFoundForWithdrawal() {
            UUID accountNumber = UUID.randomUUID();
            BigDecimal amount = BigDecimal.valueOf(1000);
            CreateTransactionDto createTransactionDto = new CreateTransactionDto(TransactionType.WITHDRAW, accountNumber, null, amount);

            when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.empty());

            DataNotFoundException exception = assertThrows(DataNotFoundException.class, () ->
                    transactionService.createTransaction(createTransactionDto)
            );

            assertThat(exception.getMessage()).isEqualTo("Source account not found.");

            verify(accountRepository, times(1)).findByAccountNumber(accountNumber);
            verifyNoInteractions(transactionRepository);
        }
    }

    @Nested
    @DisplayName("Transfer Transaction Tests")
    class TransferTransactionTests {

        @Test
        @DisplayName("Should successfully transfer funds between accounts")
        void shouldTransferFundsSuccessfully() {
            UUID fromAccountNumber = UUID.randomUUID();
            UUID toAccountNumber = UUID.randomUUID();
            BigDecimal amount = BigDecimal.valueOf(1000);
            CreateTransactionDto createTransactionDto = new CreateTransactionDto(TransactionType.TRANSFER, fromAccountNumber, toAccountNumber, amount);

            Account fromAccount = new Account(1L, fromAccountNumber, "User1", BigDecimal.valueOf(5000), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());
            Account toAccount = new Account(2L, toAccountNumber, "User2", BigDecimal.valueOf(2000), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());
            Account updatedFromAccount = new Account(1L, fromAccountNumber, "User1", BigDecimal.valueOf(4000), fromAccount.getCreateDateTime(), DateUtils.nowTimestamp());
            Account updatedToAccount = new Account(2L, toAccountNumber, "User2", BigDecimal.valueOf(3000), toAccount.getCreateDateTime(), DateUtils.nowTimestamp());

            Transaction transaction = new Transaction(1L, toAccountNumber, fromAccountNumber, amount, TransactionType.TRANSFER, DateUtils.nowTimestamp());
            TransactionDto transactionDto = new TransactionDto(1L, toAccountNumber, fromAccountNumber, amount, TransactionType.TRANSFER, DateUtils.nowUTC());

            when(accountRepository.findByAccountNumber(fromAccountNumber)).thenReturn(Optional.of(fromAccount));
            when(accountRepository.findByAccountNumber(toAccountNumber)).thenReturn(Optional.of(toAccount));
            when(accountRepository.saveAll(List.of(toAccount, fromAccount))).thenReturn(List.of(updatedToAccount, updatedFromAccount));
            when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
            when(transactionMapper.mapToDto(transaction)).thenReturn(transactionDto);

            TransactionDto result = transactionService.createTransaction(createTransactionDto);

            assertNotNull(result, "Result should not be null");
            assertThat(result.getAccountNumberFrom()).isEqualTo(fromAccountNumber);
            assertThat(result.getAccountNumberTo()).isEqualTo(toAccountNumber);
            assertThat(result.getAmount()).isEqualTo(amount);
            assertThat(result.getTransactionType()).isEqualTo(TransactionType.TRANSFER);

            verify(accountRepository, times(1)).findByAccountNumber(fromAccountNumber);
            verify(accountRepository, times(1)).findByAccountNumber(toAccountNumber);
            verify(accountRepository, times(1)).saveAll(argThat((List<Account> accounts) ->
                    accounts.contains(fromAccount) && accounts.contains(toAccount)));
            verify(transactionRepository, times(1)).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Should throw exception when sender account is not found")
        void shouldThrowExceptionWhenSenderAccountNotFound() {
            UUID fromAccountNumber = UUID.randomUUID();
            UUID toAccountNumber = UUID.randomUUID();
            BigDecimal amount = BigDecimal.valueOf(1000);
            CreateTransactionDto createTransactionDto = new CreateTransactionDto(TransactionType.TRANSFER, fromAccountNumber, toAccountNumber, amount);

            Account toAccount = new Account(1L, toAccountNumber, "User1", BigDecimal.valueOf(5000), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());
            when(accountRepository.findByAccountNumber(toAccountNumber)).thenReturn(Optional.of(toAccount));
            when(accountRepository.findByAccountNumber(fromAccountNumber)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(DataNotFoundException.class, () -> transactionService.createTransaction(createTransactionDto));

            assertThat(exception.getMessage()).isEqualTo("Source account not found.");

            verify(accountRepository, times(1)).findByAccountNumber(toAccountNumber);
            verify(accountRepository, times(1)).findByAccountNumber(fromAccountNumber);
            verifyNoInteractions(transactionRepository);
        }

        @Test
        @DisplayName("Should throw exception when receiver account is not found")
        void shouldThrowExceptionWhenReceiverAccountNotFound() {
            UUID fromAccountNumber = UUID.randomUUID();
            UUID toAccountNumber = UUID.randomUUID();
            BigDecimal amount = BigDecimal.valueOf(1000);
            CreateTransactionDto createTransactionDto = new CreateTransactionDto(TransactionType.TRANSFER, fromAccountNumber, toAccountNumber, amount);

            when(accountRepository.findByAccountNumber(toAccountNumber)).thenReturn(Optional.empty());

            RuntimeException exception = assertThrows(DataNotFoundException.class, () -> transactionService.createTransaction(createTransactionDto));

            assertThat(exception.getMessage()).isEqualTo("Target account not found.");

            verify(accountRepository, times(1)).findByAccountNumber(toAccountNumber);
            verifyNoInteractions(transactionRepository);
        }

        @Test
        @DisplayName("Should throw exception when sender has insufficient funds")
        void shouldThrowExceptionWhenSenderHasInsufficientFunds() {
            UUID fromAccountNumber = UUID.randomUUID();
            UUID toAccountNumber = UUID.randomUUID();
            BigDecimal amount = BigDecimal.valueOf(10000);
            CreateTransactionDto createTransactionDto = new CreateTransactionDto(TransactionType.TRANSFER, fromAccountNumber, toAccountNumber, amount);

            Account fromAccount = new Account(1L, fromAccountNumber, "User1", BigDecimal.valueOf(5000), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());
            Account toAccount = new Account(2L, toAccountNumber, "User2", BigDecimal.valueOf(2000), DateUtils.nowTimestamp(), DateUtils.nowTimestamp());

            when(accountRepository.findByAccountNumber(fromAccountNumber)).thenReturn(Optional.of(fromAccount));
            when(accountRepository.findByAccountNumber(toAccountNumber)).thenReturn(Optional.of(toAccount));

            RuntimeException exception = assertThrows(BankingException.class, () -> transactionService.createTransaction(createTransactionDto));

            assertThat(exception.getMessage()).isEqualTo("Insufficient funds.");

            verify(accountRepository, times(1)).findByAccountNumber(fromAccountNumber);
            verify(accountRepository, times(1)).findByAccountNumber(toAccountNumber);
            verifyNoInteractions(transactionRepository);
        }
    }
}
