package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.event.PaymentEventDto;
import faang.school.paymentservice.mapper.PaymentRequestMapper;
import faang.school.paymentservice.model.OperationType;
import faang.school.paymentservice.model.PaymentRequest;
import faang.school.paymentservice.redis.PaymentEventPublisher;
import faang.school.paymentservice.repository.PaymentRequestJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final long CLEARING_DELAY = 15;
    private static final TemporalUnit CLEARING_DELAY_TEMPORAL_UNIT = ChronoUnit.MINUTES;
    private final PaymentRequestJpaRepository repository;
    private final PaymentRequestMapper paymentRequestMapper;
    private final PaymentEventPublisher paymentEventPublisher;

    @Transactional
    public PaymentResponse sendPayment(PaymentRequestDto dto) {
        PaymentRequest pendingRequest = repository.save(createPendingRequest(dto));

        publishPaymentEvent(pendingRequest, OperationType.AUTHORIZATION);

        return formAuthorizationResponse(pendingRequest);
    }

    @Scheduled(cron = "${clear-scheduler.cron}")
    public void autoClearRequest() {
        List<PaymentRequest> pendingRequests = repository.findPaymentRequestsByIsClearedIsFalse();

        pendingRequests.stream()
                .filter(PaymentRequest::isClearScheduledAtNow)
                .forEach(this::clearRequest);
    }

    @Async
    @Transactional
    public void clearRequest(PaymentRequest pendingRequest) {
        publishPaymentEvent(pendingRequest, OperationType.CONFIRMATION);

        repository.save(pendingRequest);
    }

    private PaymentResponse formAuthorizationResponse(PaymentRequest pendingRequest) {
        return formResponse(pendingRequest, PaymentStatus.AUTHORIZATION, "To confirm payment send a confirmation request.");
    }

    private PaymentResponse formCancelingResponse(PaymentRequest pendingRequest) {
        return formResponse(pendingRequest, PaymentStatus.CANCELED, "Your payment has been canceled.");
    }

    private PaymentResponse formSucceedResponse(PaymentRequest pendingRequest) {
        return formResponse(pendingRequest, PaymentStatus.SUCCESS, "Your payment has been confirmed and succeed.");
    }

    private PaymentResponse formResponse(PaymentRequest pendingRequest, PaymentStatus status, String message) {
        PaymentResponse response = paymentRequestMapper.toPaymentResponse(pendingRequest);
        response.setStatus(status);
        response.setVerificationCode(PaymentResponse.generateVerificationCode());
        response.setMessage(message);

        return response;
    }

    private void publishPaymentEvent(PaymentRequest pendingRequest, OperationType operationType) {
        pendingRequest.setType(operationType);
        PaymentEventDto paymentEvent = paymentRequestMapper.toPaymentEvent(pendingRequest);
        paymentEventPublisher.publish(paymentEvent);
    }

    private PaymentRequest createPendingRequest(PaymentRequestDto dto) {
        PaymentRequest pendingRequest = paymentRequestMapper.toModel(dto);
        pendingRequest.setType(OperationType.AUTHORIZATION);
        pendingRequest.setIsCleared(false);
        pendingRequest.setClearScheduledAt(LocalDateTime.now().plus(CLEARING_DELAY, CLEARING_DELAY_TEMPORAL_UNIT));

        return pendingRequest;
    }
}
