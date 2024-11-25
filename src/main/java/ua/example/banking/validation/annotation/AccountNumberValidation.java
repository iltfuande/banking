package ua.example.banking.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import ua.example.banking.validation.AccountNumberValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AccountNumberValidator.class)
public @interface AccountNumberValidation {

    String message() default "At least one account number ('from' or 'to') must be provided.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
