package ua.example.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.example.banking.model.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
