package faang.school.paymentservice.service;

import faang.school.paymentservice.config.async.AbstractExecutorConfig;
import faang.school.paymentservice.config.context.UserContext;
import faang.school.paymentservice.dto.PaymentOperationRequest;
import faang.school.paymentservice.dto.event.RequestOutboxEvent;
import faang.school.paymentservice.entity.OperationType;
import faang.school.paymentservice.mapper.PaymentOperationMapper;
import faang.school.paymentservice.mapper.RequestOutboxMapper;
import faang.school.paymentservice.publisher.RequestOutboxPublisher;
import faang.school.paymentservice.repository.PaymentProcessingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final PaymentProcessingRepository paymentProcessingRepository;
    private final PaymentOperationMapper paymentOperationMapper;
    private final RequestOutboxMapper requestOutboxMapper;
    private final RequestOutboxPublisher paymentOperationEventPublisher;
    private final UserContext userContext;
    private final Map<String, AbstractExecutorConfig> executors;

    @Value("${payment-setting.minutes-to-auto-confirmation}")
    private int minutesUntilAutoConfirmation;

    public void initPayment(PaymentOperationRequest request) {
    }

    public void cancelPayment(UUID token) {

    }

    public void confirmPaymentForced(UUID token) {

    }

    public void getPaymentByToken(UUID token) {

    }

    @Async("requestForceConfirm")
    public void findPaymentsReadyToConfirmed() {

    }

    @Async("outboxClearing")
    public void clearOutbox() {

    }

    @Async("requestSender")
    public void sendRequestsOnProcessing() {

    }

    private RequestOutboxEvent createPaymentEvent(OperationType operationType, PaymentOperationRequest request) {
        return RequestOutboxEvent.builder()
                .idempotencyToken(UUID.randomUUID())
                .operationType(operationType)
                .senderId(userContext.getUserId())
                .receiverId(request.receiverId())
                .receiverType(request.receiverType())
                .amount(request.amount())
                .currency(request.currency())
                .build();
    }
}
