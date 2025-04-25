package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.message.AuthorizationMessage;
import faang.school.paymentservice.dto.message.CancellationMessage;
import faang.school.paymentservice.dto.message.ClearingMessage;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.PaymentOperation;
import faang.school.paymentservice.publisher.RedisEventPublisher;
import faang.school.paymentservice.repository.PaymentOperationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentOperationRepository paymentOperationRepository;
    private final RedisEventPublisher redisEventPublisher;
    private final PaymentMapper paymentMapper;

    @Transactional
    public PaymentResponse initiatePayment(@NotNull @Valid PaymentRequest request) {

        PaymentOperation paymentOperation = paymentMapper.toPaymentOperation(request);
        paymentOperation.setPaymentStatus(PaymentStatus.PENDING);
        paymentOperation = paymentOperationRepository.save(paymentOperation);
        AuthorizationMessage message = paymentMapper.toAuthorizationMessage(paymentOperation);

        PaymentResponse paymentResponse = paymentMapper.toPaymentResponse(paymentOperation);
        redisEventPublisher.send(message);

        return paymentResponse;
    }

    @Transactional
    public void cancelPayment(@NotNull UUID id) {
        PaymentOperation paymentOperation = paymentOperationRepository.findById(id).orElseThrow(() ->
        {
            log.error("Payment with id {} not found", id);
            return new EntityNotFoundException();
        });

        paymentOperation.setPaymentStatus(PaymentStatus.CANCELLED);
        paymentOperationRepository.save(paymentOperation);

        CancellationMessage message = paymentMapper.toCancellationMessage(paymentOperation);
        redisEventPublisher.send(message);
    }

    @Transactional
    public void forcedPayment(@NotNull UUID id) {
        PaymentOperation paymentOperation = paymentOperationRepository.findById(id).orElseThrow(() ->
        {
            log.error("Payment with id {} not found", id);
            return new EntityNotFoundException();
        });

        if (!paymentOperation.getPaymentStatus().equals(PaymentStatus.AUTHORIZED)) {
            throw new IllegalArgumentException("Payment with id " + id + " is not authorized");
        }

        paymentOperation.setPaymentStatus(PaymentStatus.CLEARED);
        paymentOperationRepository.save(paymentOperation);

        ClearingMessage message = paymentMapper.toClearingMessage(paymentOperation);
        redisEventPublisher.send(message);
    }
}
