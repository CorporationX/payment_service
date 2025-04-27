package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.event.PaymentEvent;
import faang.school.paymentservice.exception.EntityNotFoundException;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.PaymentOperation;
import faang.school.paymentservice.repository.PaymentOperationRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.stereotype.Service;

import java.util.UUID;

@EnableKafka
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentOperationRepository paymentOperationRepository;
    private final PaymentMapper paymentMapper;
    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public void initiatePayment(@NotNull @Valid PaymentRequest request) {
        PaymentOperation operation = paymentMapper.toPaymentOperation(request);
        operation.setPaymentStatus(PaymentStatus.PENDING);
        operation = paymentOperationRepository.save(operation);

        eventPublisher.publishEvent(new PaymentEvent(operation));

        //return paymentMapper.toPaymentResponse(operation);
    }

    @Transactional
    public void cancelPayment(@NotNull UUID id) {
        PaymentOperation operation = paymentOperationRepository.findById(id).orElseThrow(() -> {
            log.error("Payment with id {} not found", id);
            return new EntityNotFoundException("Payment with id " + id + " not found");
        });

        operation.setPaymentStatus(PaymentStatus.CANCELLED);
        paymentOperationRepository.save(operation);

        eventPublisher.publishEvent(new PaymentEvent(operation));

    }

    @Transactional
    public void forcedPayment(@NotNull UUID id) {
        PaymentOperation operation = paymentOperationRepository.findById(id).orElseThrow(() -> {
            log.error("Payment with id {} not found", id);
            return new EntityNotFoundException("Payment with id " + id + " not found");
        });

        if (!operation.getPaymentStatus().equals(PaymentStatus.AUTHORIZED)) {
            throw new IllegalArgumentException("Payment with id " + id + " is not authorized");
        }

        operation.setPaymentStatus(PaymentStatus.CLEARED);
        paymentOperationRepository.save(operation);

        eventPublisher.publishEvent(new PaymentEvent(operation));
    }
}
