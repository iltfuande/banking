package ua.example.banking.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ua.example.banking.model.dto.account.AccountDto;
import ua.example.banking.model.dto.account.CreateAccountDto;

import java.util.UUID;

public interface AccountService {

    AccountDto createAccount(CreateAccountDto createAccountDto);

    AccountDto getAccountDetails(UUID accountNumber);

    Page<AccountDto> getAccounts(PageRequest pageRequest);
}
