package ua.example.banking.service;

import ua.example.banking.model.dto.transaction.CreateTransactionDto;
import ua.example.banking.model.dto.transaction.TransactionDto;

public interface TransactionService {

    TransactionDto createTransaction(CreateTransactionDto createTransactionDto);
}
