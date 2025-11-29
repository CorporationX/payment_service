package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.exception.PaymentException;
import faang.school.paymentservice.model.BankOperation;
import faang.school.paymentservice.model.PaymentStatus;

public class PaymentValidator {

    public static void validateClearing(BankOperation bankOperation) {
        boolean firstTimeClearingCondition = bankOperation.getStatus() == PaymentStatus.AUTHORIZATION_SUCCESS;
        boolean retryClearingCondition = bankOperation.getStatus() == PaymentStatus.CLEARING_ERROR;
        if (!firstTimeClearingCondition && !retryClearingCondition) {
            throw new PaymentException("Non-idempotent clearing for an operation: %s".formatted(bankOperation.getId()));
        }
    }

    public static void validateCancel(BankOperation bankOperation) {
        boolean firstTimeCancelCondition = bankOperation.getStatus() == PaymentStatus.AUTHORIZATION_SUCCESS;
        boolean retryCancelCondition = bankOperation.getStatus() == PaymentStatus.CANCEL_ERROR;
        if (!firstTimeCancelCondition && !retryCancelCondition) {
            throw new PaymentException("Non-idempotent cancel for an operation: %s".formatted(bankOperation.getId()));
        }
    }
}
