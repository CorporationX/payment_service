package faang.school.paymentservice.service;

import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.exception.DuplicatePaymentException;
import faang.school.paymentservice.exception.PaymentOperationException;
import faang.school.paymentservice.repository.PaymentHashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentCheckService {
    private final PaymentHashRepository paymentHashRepository;
    @Value("${spring.kafka.producer.request-retries}")
    private int retryCount = 5;

    public void checkNewPayment(Payment payment) {
        if (!paymentHashRepository.isNewPayment(payment)) {
            throw new DuplicatePaymentException("Payment %s already exists".formatted(payment.getId()));
        }
    }

    public void addNewPayment(Payment payment) {
        paymentHashRepository.insertPaymentHash(payment);
    }

    public boolean canAuthorizePayment(Payment payment) throws PaymentOperationException {
        long count = paymentHashRepository.getAuthMessageCount(payment.getId());
        if (count >= retryCount || payment.getPaymentStatus() != PaymentStatus.NEW) {
            throw new PaymentOperationException("Payment %s is not New or send %d authorization message retries"
                    .formatted(payment.getId(), count));
        }
        return count == 0;
    }

    public void incrementAuthorizationMessageCount(UUID id) {
        paymentHashRepository.incrementAuthMessageCount(id);
    }

    public boolean isPaymentClearable(Payment payment) throws PaymentOperationException {
        long count = paymentHashRepository.getClearingMessageCount(payment.getId());
        if (count >= retryCount || (payment.getPaymentStatus() != PaymentStatus.PROCESS_OF_CLEARING
                && payment.getPaymentStatus() != PaymentStatus.AUTHORIZED)) {
            throw new PaymentOperationException("Payment %s can not clearing. Payment send %d clearing message retries"
                    .formatted(payment.getId(), count));
        }
        return count == 0;
    }

    public void incrementClearingMessageCount(UUID id) {
        paymentHashRepository.incrementClearingMessageCount(id);
    }

    public boolean isPaymentCancellable(Payment payment) throws PaymentOperationException {
        long count = paymentHashRepository.getCancelMessageCount(payment.getId());
        if (count >= retryCount || payment.getPaymentStatus() != PaymentStatus.PROCESS_OF_CANCELLATION) {
            throw new PaymentOperationException("Payment %s can not cancel. Payment send %d cancel message retries"
                    .formatted(payment.getId(), count));
        }
        return count == 0;
    }

    public void incrementCancelMessageCount(UUID id) {
        paymentHashRepository.incrementCancelMessageCount(id);
    }

    public void deleteAuthorizationMessageCounter(UUID id) {
        paymentHashRepository.deleteAuthMessageCount(id);
    }

    public void deleteClearingMessageCounter(UUID id) {
        paymentHashRepository.deleteClearingMessageCount(id);
    }

    public void deleteCancelMessageCounter(UUID id) {
        paymentHashRepository.deleteCancelMessageCount(id);
    }

}
