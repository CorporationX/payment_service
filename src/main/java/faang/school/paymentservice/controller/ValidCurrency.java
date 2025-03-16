package faang.school.paymentservice.controller;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD, ElementType.PARAMETER } )
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CurrencyValidator.class)
public @interface ValidCurrency {

    String message() default "Not support currency";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

