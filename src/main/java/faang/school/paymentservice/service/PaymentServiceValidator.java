package faang.school.paymentservice.service;

import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.model.OperationType;
import faang.school.paymentservice.model.PaymentRequest;
import org.springframework.stereotype.Component;

@Component
class PaymentServiceValidator {
    public void validateRequestBeforeClearing(PaymentRequest paymentRequest) {
        if (!paymentRequest.getType().equals(OperationType.AUTHORIZATION)) {
            throw new DataValidationException("Only pending operations (with \"AUTHORIZATION\" status) can be canceled.");
        }

        if (paymentRequest.getIsCleared().equals(true)) {
            throw new DataValidationException("This operation is already finished.");
        }
    }
}
