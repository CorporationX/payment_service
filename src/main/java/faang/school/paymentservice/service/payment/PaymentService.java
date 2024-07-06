package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.config.DynamicScheduledAtTimeConfig;
import faang.school.paymentservice.dto.InvoiceDto;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.event.payment.PaymentEvent;
import faang.school.paymentservice.exception.PaymentAlreadyCreatedException;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.publisher.PaymentEventPublisher;
import faang.school.paymentservice.repository.PaymentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentValidator paymentValidator;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher paymentEventPublisher;
    private final DynamicScheduledAtTimeConfig scheduledAtTimeConfig;

    @Transactional
    public PaymentResponse create(InvoiceDto invoiceDto) {
        paymentValidator.checkInvoiceFields(invoiceDto);
        String idempotencyKey = invoiceDto.getIdempotencyKey().toString();
        if (paymentRepository.existsByIdempotencyKey(idempotencyKey)) {
            throw new PaymentAlreadyCreatedException(String.format("payment with key %s already exists", idempotencyKey));
        }
        Payment payment = createPayment(invoiceDto);
        log.info("payment {} created", payment);
        sendEvent(payment);
        PaymentResponse paymentResponse = paymentMapper.toResponse(payment);
        paymentResponse.setMessage("payment created successfully");
        return paymentResponse;
    }

    @Transactional
    public PaymentResponse cancel(Long paymentId) {
        Payment payment = findById(paymentId);
        payment.setStatus(PaymentStatus.CANCELED);
        if (payment.getScheduledAt() != null) {
            payment.setScheduledAt(null);
        }
        payment = paymentRepository.save(payment);
        log.info("payment {} canceled", payment);
        sendEvent(payment);
        PaymentResponse paymentResponse = paymentMapper.toResponse(payment);
        paymentResponse.setMessage("payment canceled");
        return paymentResponse;
    }

    @Transactional
    public PaymentResponse clear(Long paymentId) {
        Payment payment = findById(paymentId);
        payment.setStatus(PaymentStatus.CLEARED);
        payment = paymentRepository.save(payment);
        log.info("payment {} cleared", payment);
        sendEvent(payment);
        PaymentResponse paymentResponse = paymentMapper.toResponse(payment);
        paymentResponse.setMessage("payment cleared");
        return paymentMapper.toResponse(payment);
    }

    @Transactional
    public PaymentResponse success(Long paymentId) {
        Payment payment = findById(paymentId);
        if (payment.getStatus().equals(PaymentStatus.CLEARED)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment = paymentRepository.save(payment);
            log.info("payment {} passed successfully", payment);
            PaymentResponse paymentResponse = paymentMapper.toResponse(payment);
            paymentResponse.setMessage("payment passed successfully");
            return paymentMapper.toResponse(payment);
        } else {
            throw new IllegalStateException("payment still not cleared");
        }
    }

    public List<Payment> getScheduledPayments() {
        return paymentRepository.findAllScheduledPayments();
    }

    private Payment createPayment(InvoiceDto invoiceDto) {
        Payment payment = Payment.builder()
                .requesterNumber(invoiceDto.getRequesterNumber())
                .receiverNumber(invoiceDto.getReceiverNumber())
                .currency(invoiceDto.getCurrency())
                .amount(invoiceDto.getAmount())
                .status(PaymentStatus.AUTHORIZATION)
                .build();
        payment.setScheduledAt(scheduledAtTimeConfig.getScheduledAt());
        return paymentRepository.save(payment);
    }

    private Payment findById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new EntityNotFoundException(String.format("payment with id = %d not found", paymentId)));
    }

    private void sendEvent(Payment payment) {
        PaymentEvent paymentEvent = paymentMapper.toEvent(payment);
        paymentEventPublisher.publish(paymentEvent);
        log.info("event {} was send", paymentEvent);
    }
}