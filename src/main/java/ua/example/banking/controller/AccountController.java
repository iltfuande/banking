package ua.example.banking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.example.banking.model.dto.account.AccountDto;
import ua.example.banking.model.dto.account.CreateAccountDto;
import ua.example.banking.service.AccountService;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account Controller", description = "API for managing accounts")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create an account", description = "Adds a new account to the system")
    public AccountDto createAccount(@Valid @RequestBody CreateAccountDto createAccountDto) {
        return accountService.createAccount(createAccountDto);
    }

    @GetMapping("/{accountNumber}")
    @Operation(summary = "Get account details", description = "Returns information about an account by its number")
    public AccountDto getAccountDetails(@PathVariable UUID accountNumber) {
        return accountService.getAccountDetails(accountNumber);
    }

    @GetMapping
    @Operation(summary = "List accounts", description = "Retrieves a paginated list of accounts")
    public Page<AccountDto> getAccounts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        return accountService.getAccounts(PageRequest.of(page, size));
    }
}
