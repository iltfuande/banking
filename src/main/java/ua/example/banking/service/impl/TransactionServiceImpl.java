package ua.example.banking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.example.banking.advice.exception.DataNotFoundException;
import ua.example.banking.advice.exception.ValidationException;
import ua.example.banking.mappers.TransactionMapper;
import ua.example.banking.model.dto.transaction.CreateTransactionDto;
import ua.example.banking.model.dto.transaction.TransactionDto;
import ua.example.banking.model.entity.Account;
import ua.example.banking.model.entity.Transaction;
import ua.example.banking.model.enums.TransactionType;
import ua.example.banking.repository.AccountRepository;
import ua.example.banking.repository.TransactionRepository;
import ua.example.banking.service.TransactionService;
import ua.example.banking.util.DateUtils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public TransactionDto createTransaction(CreateTransactionDto createTransactionDto) {
        log.info("Creating transaction of type: '{}'.", createTransactionDto.getTransactionType());

        Transaction transaction;
        switch (createTransactionDto.getTransactionType()) {
            case DEPOSIT -> transaction = depositFunds(createTransactionDto);
            case WITHDRAW -> transaction = withdrawFunds(createTransactionDto);
            case TRANSFER -> transaction = transferFunds(createTransactionDto);
            default -> throw new IllegalArgumentException("Invalid transaction type");
        }

        log.info("Transaction of type: '{}' created successfully.",
                transaction.getTransactionType());

        return transactionMapper.mapToDto(transaction);
    }

    private Transaction depositFunds(CreateTransactionDto createTransactionDto) {
        UUID to = createTransactionDto.getTo();
        BigDecimal amount = createTransactionDto.getAmount();

        log.info("Initiating deposit of amount '{}' to account: '{}'.", amount, to);

        Account toAccount = accountRepository.findByAccountNumber(to)
                .orElseThrow(() -> new DataNotFoundException("Target account not found."));

        Timestamp now = DateUtils.nowTimestamp();
        toAccount.setBalance(toAccount.getBalance().add(amount));
        toAccount.setUpdateDateTime(now);
        accountRepository.save(toAccount);

        log.info("Deposit of amount '{}' to account '{}' completed successfully. Updated balance: '{}'.",
                amount, to, toAccount.getBalance());

        Transaction transaction = transactionRepository.save(
                new Transaction(to, null, amount, TransactionType.DEPOSIT, now)
        );

        log.info("Transaction record for deposit created successfully. Transaction ID: '{}'.", transaction.getId());

        return transaction;
    }

    private Transaction withdrawFunds(CreateTransactionDto createTransactionDto) {
        UUID from = createTransactionDto.getFrom();
        BigDecimal amount = createTransactionDto.getAmount();

        log.info("Initiating withdrawal of amount '{}' from account: '{}'.", amount, from);

        Account fromAccount = accountRepository.findByAccountNumber(from)
                .orElseThrow(() -> new DataNotFoundException("Source account not found."));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            log.error("Insufficient funds for withdrawal from account '{}'. Available balance: '{}', requested: '{}'.",
                    from, fromAccount.getBalance(), amount);
            throw new ValidationException("Insufficient funds.");
        }

        Timestamp now = DateUtils.nowTimestamp();
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        fromAccount.setUpdateDateTime(now);
        accountRepository.save(fromAccount);

        log.info("Withdrawal of amount '{}' from account '{}' completed successfully. Updated balance: '{}'.",
                amount, from, fromAccount.getBalance());

        Transaction transaction = transactionRepository.save(
                new Transaction(null, from, amount, TransactionType.WITHDRAW, now)
        );

        log.info("Transaction record for withdrawal created successfully. Transaction ID: '{}'.", transaction.getId());

        return transaction;
    }

    private Transaction transferFunds(CreateTransactionDto createTransactionDto) {
        UUID to = createTransactionDto.getTo();
        UUID from = createTransactionDto.getFrom();
        BigDecimal amount = createTransactionDto.getAmount();

        log.info("Initiating transfer of amount '{}' from account '{}' to account '{}'.", amount, from, to);

        Account toAccount = accountRepository.findByAccountNumber(to)
                .orElseThrow(() -> new DataNotFoundException("Target account not found."));
        Account fromAccount = accountRepository.findByAccountNumber(from)
                .orElseThrow(() -> new DataNotFoundException("Source account not found."));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            log.error("Insufficient funds for transfer from account '{}'. Available balance: '{}', requested: '{}'.",
                    from, fromAccount.getBalance(), amount);
            throw new ValidationException("Insufficient funds.");
        }

        Timestamp now = DateUtils.nowTimestamp();
        toAccount.setBalance(toAccount.getBalance().add(amount));
        toAccount.setUpdateDateTime(now);
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        fromAccount.setUpdateDateTime(now);
        accountRepository.saveAll(List.of(toAccount, fromAccount));

        log.info("Transfer of amount '{}' from account '{}' to account '{}' completed successfully. Updated balances - From: '{}', To: '{}'.",
                amount, from, to, fromAccount.getBalance(), toAccount.getBalance());

        Transaction transaction = transactionRepository.save(
                new Transaction(to, from, amount, TransactionType.TRANSFER, now)
        );

        log.info("Transaction record for transfer created successfully. Transaction ID: '{}'.", transaction.getId());

        return transaction;
    }
}
