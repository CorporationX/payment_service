package faang.school.paymentservice.validator.payment;

import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentValidator {

    private final PaymentRepository paymentRepository;

    public void validateIdempotencyKeyIsUnique(String idempotencyKey) {
        if (idempotencyKey == null) {
            throw new DataValidationException("Idempotency key cannot be null.");
        }

        Optional<Payment> existingPayment = paymentRepository.findByIdempotencyKey(idempotencyKey);
        if (existingPayment.isPresent()) {
            throw new DataValidationException(
                    String.format("Payment with idempotency key '%s' already exists.", idempotencyKey)
            );
        }
    }

    public void validateDifferentAccounts(Payment payment) {
        if (payment.getSourceAccountId().equals(payment.getTargetAccountId())) {
            throw new DataValidationException("Source and target account IDs cannot be the same.");
        }
    }

    public void validateStatusForUpdateEligibility(Payment payment) {
        EnumSet<PaymentStatus> nonUpdatableStatuses = EnumSet.of(
                PaymentStatus.CONFIRM_PENDING,
                PaymentStatus.CANCEL_PENDING,
                PaymentStatus.CLEAR_PENDING
        );

        if (nonUpdatableStatuses.contains(payment.getStatus())) {
            throw new DataValidationException(
                    String.format("Payments with status '%s' cannot be updated.", payment.getStatus().name())
            );
        }
    }

    public void validateCancelableStatus(Payment payment) {
        if (payment.getStatus() != PaymentStatus.AUTH_SUCCESS) {
            throw new DataValidationException("Only payments with 'AUTH_SUCCESS' status can be canceled.");
        }
    }

    public void validateConfirmableStatus(Payment payment) {
        if (payment.getStatus() != PaymentStatus.AUTH_SUCCESS) {
            throw new DataValidationException("Only payments with 'AUTH_SUCCESS' status can be confirmed.");
        }
    }
}


