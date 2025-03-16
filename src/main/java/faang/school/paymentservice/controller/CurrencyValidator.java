package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class CurrencyValidator implements ConstraintValidator<ValidCurrency, Currency> {

    @Override
    public void initialize(ValidCurrency constraintAnnotation) {
    }

    @Override
    public boolean isValid(Currency value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        try {
            Currency.valueOf(value.name());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

