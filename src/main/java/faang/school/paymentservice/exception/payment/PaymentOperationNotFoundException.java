package faang.school.paymentservice.exception.payment;

import jakarta.persistence.EntityNotFoundException;

public class PaymentOperationNotFoundException extends EntityNotFoundException {
    public PaymentOperationNotFoundException(String msg) {
        super(msg);
    }
}
