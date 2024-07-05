package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.event.PaymentEventDto;
import faang.school.paymentservice.mapper.PaymentRequestMapper;
import faang.school.paymentservice.model.OperationType;
import faang.school.paymentservice.model.PaymentRequest;
import faang.school.paymentservice.redis.PaymentEventPublisher;
import faang.school.paymentservice.repository.PaymentRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final TemporalUnit CLEARING_DELAY_TEMPORAL_UNIT = ChronoUnit.MINUTES;
    @Value("${clear-scheduler.auto-clear-delay-amount}")
    private long clearingDelay;
    private final PaymentRequestRepository repository;
    private final PaymentRequestMapper mapper;
    private final PaymentEventPublisher eventPublisher;
    private final PaymentServiceValidator verifier;


    @Transactional
    public PaymentResponse sendPayment(PaymentRequestDto dto) {
        PaymentRequest pendingRequest = repository.save(createPendingRequest(dto));
        publishPaymentEvent(pendingRequest);

        return formAuthorizationResponse(pendingRequest);
    }

    @Transactional
    public PaymentResponse cancelPayment(Long paymentId) {
        PaymentRequest pendingRequestForClearing = getPendingRequest(paymentId);

        clearRequest(pendingRequestForClearing, OperationType.CANCELING);

        return formCancelingResponse(pendingRequestForClearing);
    }

    @Transactional
    public PaymentResponse confirmPayment(Long paymentId, BigDecimal newAmount) {
        PaymentRequest pendingRequest = getPendingRequest(paymentId);

        if (newAmount != null) {
            pendingRequest.setAmount(newAmount);
        }

        clearRequest(pendingRequest, OperationType.CONFIRMATION);

        return formSucceedResponse(pendingRequest);
    }

    @Scheduled(cron = "${clear-scheduler.cron}")
    public void autoClearRequest() {
        List<PaymentRequest> pendingRequests = repository.findPendingPaymentRequests();

        pendingRequests.stream()
                .filter(PaymentRequest::shouldClearNow)
                .forEach(pendingRequest -> clearRequest(pendingRequest, OperationType.CONFIRMATION));
    }

    @Async
    @Transactional
    public void clearRequest(PaymentRequest pendingRequest, OperationType operationType) {
        pendingRequest.setIsCleared(true);
        pendingRequest.setType(operationType);
        repository.save(pendingRequest);

        publishPaymentEvent(pendingRequest);
    }

    private PaymentRequest getPendingRequest(Long paymentId) {
        PaymentRequest pendingRequest = repository.findById(paymentId);

        verifier.validateRequestBeforeClearing(pendingRequest);
        return pendingRequest;
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
        PaymentResponse response = mapper.toPaymentResponse(pendingRequest);
        response.setStatus(status);
        response.setMessage(message);

        return response;
    }

    private void publishPaymentEvent(PaymentRequest pendingRequest) {
        PaymentEventDto paymentEvent = mapper.toPaymentEvent(pendingRequest);
        eventPublisher.publish(paymentEvent);
    }

    private PaymentRequest createPendingRequest(PaymentRequestDto dto) {
        PaymentRequest pendingRequest = mapper.toModel(dto);
        pendingRequest.setType(OperationType.AUTHORIZATION);
        pendingRequest.setIsCleared(false);
        pendingRequest.setClearScheduledAt(LocalDateTime.now().plus(clearingDelay, CLEARING_DELAY_TEMPORAL_UNIT));

        return pendingRequest;
    }
}