package faang.school.paymentservice.service.impl;

import faang.school.paymentservice.client.AccountServiceClient;
import faang.school.paymentservice.config.context.UserContext;
import faang.school.paymentservice.exception.OperationNotPermittedException;
import faang.school.paymentservice.mapper.PendingOperationMapper;
import faang.school.paymentservice.model.dto.PaymentRequest;
import faang.school.paymentservice.model.dto.PendingOperationDto;
import faang.school.paymentservice.model.entity.PendingOperation;
import faang.school.paymentservice.model.enums.OperationType;
import faang.school.paymentservice.model.enums.PaymentStatus;
import faang.school.paymentservice.model.event.PaymentEvent;
import faang.school.paymentservice.publisher.PaymentEventPublisher;
import faang.school.paymentservice.repository.PendingOperationRepository1;
import faang.school.paymentservice.service.PendingOperationService;
import faang.school.paymentservice.validator.ValidatorPaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PendingOperationServiceImpl implements PendingOperationService {
    private static final int TIME_BEFORE_CLEARING_IN_MINUTES = 30;
    private static final long PAYMENT_NUMBER_FOR_JUST_CONVERTING = -1L;

    private final PendingOperationRepository1 pendingOperationRepository;
    private final ValidatorPaymentService validatorPaymentService;
    private final PendingOperationMapper pendingOperationMapper;
    private final AccountServiceClient accountServiceClient;
    private final UserContext userContext;
    private final CurrencyConverter currencyConverter;
    private final PaymentEventPublisher paymentEventPublisher;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public UUID createPayment(PendingOperationDto pendingOperationDto) {
        Optional<PendingOperation> pendingOperationOptional = pendingOperationRepository.findById(pendingOperationDto.getId());
        if (pendingOperationOptional.isPresent() && isPendingOperationTheSame(pendingOperationOptional.get(), pendingOperationDto)) {
            return pendingOperationDto.getId();
        } else {
            pendingOperationDto.setStatus(PaymentStatus.PENDING);
            pendingOperationDto.setClearScheduledAt(LocalDateTime.now().plusMinutes(TIME_BEFORE_CLEARING_IN_MINUTES));
            BigDecimal amountInReceiverCurrency = calculateAmountInReceiverCurrency(pendingOperationDto);
            pendingOperationDto.setAmountInReceiverCurrency(amountInReceiverCurrency);
            PendingOperation pendingOperation = pendingOperationMapper.toEntity(pendingOperationDto);
            pendingOperationRepository.save(pendingOperation);

            publishPaymentEvent(pendingOperation, OperationType.AUTHORIZATION);
        }
        return pendingOperationDto.getId();
    }

    @Override
    @Transactional
    public void cancelPayment(UUID pendingOperationId) {
        PendingOperation pendingOperation = pendingOperationRepository.findById(pendingOperationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Payment with ID %s not found", pendingOperationId))
        );
        Set<PaymentStatus> allowedStatus = EnumSet.of(PaymentStatus.PENDING, PaymentStatus.IN_PROGRESS);
        if (!allowedStatus.contains(pendingOperation.getStatus())) {
            throw new OperationNotPermittedException(String.format("It's prohibited to cancel " +
                    "payment in status %s", pendingOperation.getStatus()));
        }
        publishPaymentEvent(pendingOperation, OperationType.CANCEL);
    }

    @Override
    public PendingOperationDto getPendingOperation(UUID pendingOperationId) {
        PendingOperation pendingOperation = pendingOperationRepository.findById(pendingOperationId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Pending operation with ID %s not found", pendingOperationId)));
        return pendingOperationMapper.toDto(pendingOperation);
    }

    @Retryable(
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private void publishPaymentEvent(PendingOperation pendingOperation, OperationType operationType) {
        applicationEventPublisher.publishEvent(PaymentEvent.builder()
                .idempotencyToken(pendingOperation.getId())
                .senderContextUserId(userContext.getUserId())
                .senderAccountId(pendingOperation.getSenderAccountId())
                .recipientAccountId(pendingOperation.getReceiverAccountId())
                .operationType(operationType)
                .amount(pendingOperation.getAmountInReceiverCurrency())
                .sentDateTime(LocalDateTime.now())
                .build());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onRequestCompleted(PaymentEvent event) {
        paymentEventPublisher.publish(event);
    }

    private BigDecimal calculateAmountInReceiverCurrency(PendingOperationDto pendingOperationDto) {
        PaymentRequest paymentRequest = new PaymentRequest(
                PAYMENT_NUMBER_FOR_JUST_CONVERTING,
                pendingOperationDto.getAmountInSenderCurrency(),
                pendingOperationDto.getSenderCurrency());
        return currencyConverter.getLatestExchangeRates(paymentRequest, pendingOperationDto.getReceiverCurrency());
    }

    private boolean isPendingOperationTheSame(PendingOperation pendingOperation, PendingOperationDto pendingOperationDto) {
        return pendingOperation.getSenderAccountId().equals(pendingOperationDto.getSenderAccountId()) &&
                pendingOperation.getReceiverAccountId().equals(pendingOperationDto.getReceiverAccountId()) &&
                pendingOperation.getAmountInSenderCurrency().equals(pendingOperationDto.getAmountInSenderCurrency());
    }
}
