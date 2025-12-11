package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.exception.DuplicateRequestException;
import faang.school.paymentservice.model.Transfer;
import faang.school.paymentservice.model.PaymentStatus;

public class PaymentValidator {

    public static void validateClearing(Transfer transfer) {
        boolean clearingCondition = transfer.getStatus() == PaymentStatus.AUTHORIZATION_SUCCESS;
        if (!clearingCondition) {
            throw new DuplicateRequestException("Non-idempotent clearing for an operation: %s".formatted(transfer.getId()));
        }
    }

    public static void validateCancel(Transfer transfer) {
        boolean cancelCondition = transfer.getStatus() == PaymentStatus.AUTHORIZATION_SUCCESS;
        if (!cancelCondition) {
            throw new DuplicateRequestException("Non-idempotent cancel for an operation: %s".formatted(transfer.getId()));
        }
    }
}
