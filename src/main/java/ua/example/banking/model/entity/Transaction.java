package ua.example.banking.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.example.banking.model.enums.TransactionType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private UUID accountNumberTo;

    @Column
    private UUID accountNumberFrom;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column
    private Timestamp createDateTime;

    public Transaction(UUID accountNumberTo,
                       UUID accountNumberFrom,
                       BigDecimal amount,
                       TransactionType transactionType,
                       Timestamp createDateTime) {
        this.accountNumberTo = accountNumberTo;
        this.accountNumberFrom = accountNumberFrom;
        this.amount = amount;
        this.transactionType = transactionType;
        this.createDateTime = createDateTime;
    }
}
