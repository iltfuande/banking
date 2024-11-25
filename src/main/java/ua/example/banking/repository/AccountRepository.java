package ua.example.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.example.banking.model.entity.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(UUID accountNumber);
}
