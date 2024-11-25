package ua.example.banking.mappers;

import org.mapstruct.Mapper;
import ua.example.banking.model.dto.transaction.TransactionDto;
import ua.example.banking.model.entity.Transaction;
import ua.example.banking.util.DateUtils;

@Mapper(uses = DateUtils.class)
public interface TransactionMapper {

    TransactionDto mapToDto(Transaction transaction);
}