package faang.school.paymentservice.annotations;

import faang.school.paymentservice.model.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SendPendingOperationMessage {
    OperationType value();
}
