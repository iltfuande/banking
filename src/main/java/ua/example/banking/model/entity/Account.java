package ua.example.banking.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Generated(event = EventType.INSERT)
    private UUID accountNumber;

    @Column(length = 100, nullable = false)
    private String ownerName;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column
    private Timestamp createDateTime;

    @Column
    private Timestamp updateDateTime;
}
