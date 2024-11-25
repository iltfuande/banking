package ua.example.banking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ua.example.banking.model.enums.TransactionType;
import ua.example.banking.validation.annotation.AccountNumberValidation;

import java.util.UUID;

public class AccountNumberValidator implements ConstraintValidator<AccountNumberValidation, Object> {

    public static final String MUST_BE_DIFFERENT_MESSAGE = "Source and target accounts must be different.";

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        try {
            UUID to = (UUID) value.getClass().getMethod("getTo").invoke(value);
            UUID from = (UUID) value.getClass().getMethod("getFrom").invoke(value);
            TransactionType transactionType = (TransactionType) value.getClass().getMethod("getTransactionType").invoke(value);

            if (transactionType == TransactionType.TRANSFER && to != null && to.equals(from)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(MUST_BE_DIFFERENT_MESSAGE).addConstraintViolation();
                return false;
            }

            return from != null || to != null;
        } catch (Exception e) {
            return false;
        }
    }
}
