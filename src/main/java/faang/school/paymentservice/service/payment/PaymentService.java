package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.dto.payment.PaymentDto;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.dto.payment.PaymentCreateDto;
import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.publisher.payment.PaymentEventPublisher;
import faang.school.paymentservice.repository.PaymentRepository;
import faang.school.paymentservice.validator.payment.PaymentValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentValidator paymentValidator;
    private final PaymentEventPublisher eventPublisher;

    @Transactional
    public PaymentDto sendPayment(PaymentCreateDto dto) {
        log.info("Initiating payment: {}", dto);

        paymentValidator.validateIdempotencyKeyIsUnique(dto.getIdempotencyKey());

        Payment payment = createAndPersistPayment(dto);
        paymentValidator.validateDifferentAccounts(payment);

        eventPublisher.publishPayment(payment);

        return paymentMapper.toPaymentDto(payment);
    }

    @Transactional
    public PaymentDto cancelPayment(UUID paymentId) {
        log.info("Canceling payment with id = {}", paymentId);

        Payment payment = getPaymentById(paymentId);
        paymentValidator.validateCancelableStatus(payment);

        return updatePaymentStatusAndPublish(payment, PaymentStatus.CANCEL_PENDING);
    }

    @Transactional
    public PaymentDto confirmPayment(UUID paymentId) {
        log.info("Confirming payment with id = {}", paymentId);

        Payment payment = getPaymentById(paymentId);
        paymentValidator.validateConfirmableStatus(payment);

        return updatePaymentStatusAndPublish(payment, PaymentStatus.CONFIRM_PENDING);
    }

    @Transactional
    public void processClearingPayments() {
        List<Payment> payments = paymentRepository.getReadyForClearingPayments();

        payments.forEach(payment -> updatePaymentStatusAndPublish(payment, PaymentStatus.CLEAR_PENDING));
    }

    @Transactional
    public void updatePaymentStatusById(UUID paymentId, PaymentStatus status) {
        Payment payment = getPaymentById(paymentId);
        updatePaymentStatus(payment, status);
    }

    private Payment updatePaymentStatus(Payment payment, PaymentStatus status) {
        paymentValidator.validateStatusForUpdateEligibility(payment);

        payment.setStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    private PaymentDto updatePaymentStatusAndPublish(Payment payment, PaymentStatus status) {
        Payment updatedPayment = updatePaymentStatus(payment, status);
        eventPublisher.publishPayment(updatedPayment);

        return paymentMapper.toPaymentDto(updatedPayment);
    }

    private Payment getPaymentById(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("Payment doesn't exist with id: " + id));
    }

    private Payment createAndPersistPayment(PaymentCreateDto dto) {
        Payment payment = paymentMapper.toEntity(dto);
        payment.setIdempotencyKey(dto.getIdempotencyKey());
        payment.setStatus(PaymentStatus.AUTH_PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }
}

