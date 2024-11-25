package ua.example.banking.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ua.example.banking.advice.exception.DataNotFoundException;
import ua.example.banking.mappers.AccountMapper;
import ua.example.banking.model.dto.account.AccountDto;
import ua.example.banking.model.dto.account.CreateAccountDto;
import ua.example.banking.model.entity.Account;
import ua.example.banking.repository.AccountRepository;
import ua.example.banking.service.AccountService;
import ua.example.banking.util.DateUtils;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final AccountRepository accountRepository;

    @Override
    public AccountDto createAccount(CreateAccountDto createAccountDto) {
        Account account = accountMapper.mapToEntity(createAccountDto);
        account.setCreateDateTime(DateUtils.nowTimestamp());

        log.info("Creating account with balance {} by owner: '{}'.", account.getBalance(), account.getOwnerName());

        Account createdAccount = accountRepository.save(account);

        log.info("Account '{}' with id: '{}' created successfully by owner: '{}'.", createdAccount.getAccountNumber(),
                createdAccount.getId(), createdAccount.getOwnerName());

        return accountMapper.mapToDto(createdAccount);
    }

    @Override
    public AccountDto getAccountDetails(UUID accountNumber) {
        log.info("Retrieving account with account number: '{}'.", accountNumber);

        AccountDto accountDto = accountRepository.findByAccountNumber(accountNumber)
                .map(accountMapper::mapToDto)
                .orElseThrow(() -> new DataNotFoundException("Account with account number: %s not found."
                        .formatted(accountNumber)));

        log.info("Account with account number: '{}' retrieved successfully.", accountNumber);

        return accountDto;
    }

    @Override
    public Page<AccountDto> getAccounts(PageRequest pageRequest) {
        log.info("Retrieving accounts.");

        Page<AccountDto> accounts = accountRepository.findAll(pageRequest)
                .map(accountMapper::mapToDto);

        log.info("Accounts retrieved successfully.");

        return accounts;
    }
}
