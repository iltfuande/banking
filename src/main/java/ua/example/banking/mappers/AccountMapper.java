package ua.example.banking.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ua.example.banking.model.dto.account.AccountDto;
import ua.example.banking.model.dto.account.CreateAccountDto;
import ua.example.banking.model.entity.Account;
import ua.example.banking.util.DateUtils;

@Mapper(uses = DateUtils.class)
public interface AccountMapper {

    AccountDto mapToDto(Account account);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "createDateTime", ignore = true)
    @Mapping(target = "updateDateTime", ignore = true)
    Account mapToEntity(CreateAccountDto createAccountDto);
}