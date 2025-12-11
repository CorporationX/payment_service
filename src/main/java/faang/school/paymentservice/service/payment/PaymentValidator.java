package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.exception.DuplicateRequestException;
import faang.school.paymentservice.model.Transfer;
import faang.school.paymentservice.model.PaymentStatus;

public class PaymentValidator {

    public static void validateClearing(Transfer transfer) {
        boolean firstTimeClearingCondition = transfer.getStatus() == PaymentStatus.AUTHORIZATION_SUCCESS;
        boolean retryClearingCondition = transfer.getStatus() == PaymentStatus.CLEARING_FAIL;
        if (!firstTimeClearingCondition && !retryClearingCondition) {
            throw new DuplicateRequestException("Non-idempotent clearing for an operation: %s".formatted(transfer.getId()));
        }
    }

    public static void validateCancel(Transfer transfer) {
        boolean firstTimeCancelCondition = transfer.getStatus() == PaymentStatus.AUTHORIZATION_SUCCESS;
        boolean retryCancelCondition = transfer.getStatus() == PaymentStatus.CANCEL_FAIL;
        if (!firstTimeCancelCondition && !retryCancelCondition) {
            throw new DuplicateRequestException("Non-idempotent cancel for an operation: %s".formatted(transfer.getId()));
        }
    }
}
