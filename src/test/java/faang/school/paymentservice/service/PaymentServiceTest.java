package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.outbox.OutboxStatus;
import faang.school.paymentservice.event.PaymentEvent;
import faang.school.paymentservice.exception.EntityNotFoundException;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.PaymentOperation;
import faang.school.paymentservice.repository.OutboxEventRepository;
import faang.school.paymentservice.repository.PaymentOperationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PaymentServiceTest {

    @Mock
    private PaymentOperationRepository paymentOperationRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentRequest paymentRequest;
    private PaymentOperation paymentOperation;
    private PaymentResponse paymentResponse;
    private UUID paymentId;
    private UUID senderAccountId;
    private UUID recipientAccountId;
    private BigDecimal amount;
    private Currency currency;
    private Instant clearScheduledAt;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        paymentId = UUID.randomUUID();
        senderAccountId = UUID.randomUUID();
        recipientAccountId = UUID.randomUUID();
        amount = new BigDecimal("100.50");
        currency = Currency.USD;
        clearScheduledAt = Instant.now();

        paymentRequest = new PaymentRequest(
                senderAccountId,
                recipientAccountId,
                amount,
                currency,
                clearScheduledAt
        );

        paymentOperation = new PaymentOperation();
        paymentOperation.setId(paymentId);
        paymentOperation.setAmount(amount);
        paymentOperation.setCurrency(currency);

        paymentResponse = new PaymentResponse(
                PaymentStatus.PENDING,
                1234,
                1001L,
                amount,
                currency,
                ""
        );
    }

    @Nested
    class InitiatePaymentTest {

        @Test
        void givenValidData_whenInitiatePayment_thenSuccess() {
            when(paymentMapper.toPaymentOperation(paymentRequest)).thenReturn(paymentOperation);
            when(paymentOperationRepository.save(any(PaymentOperation.class))).thenReturn(paymentOperation);
            when(paymentMapper.toPaymentResponse(eq(paymentOperation), eq(""))).thenReturn(paymentResponse);

            PaymentResponse response = paymentService.initiatePayment(paymentRequest);

            assertNotNull(response);
            assertEquals(PaymentStatus.PENDING, response.status());
            assertEquals(amount, response.amount());
            assertEquals(currency, response.currency());
            assertEquals("", response.message());
            verify(paymentOperationRepository, times(1)).save(paymentOperation);
            verify(eventPublisher, times(1)).publishEvent(any(PaymentEvent.class));
            verify(paymentMapper, times(1)).toPaymentResponse(paymentOperation, "");
        }

        @Test
        void givenInvalidData_whenInitiatePayment_thenFailure() {
            when(paymentMapper.toPaymentOperation(paymentRequest)).thenReturn(paymentOperation);
            when(paymentOperationRepository.save(any(PaymentOperation.class)))
                    .thenThrow(new RuntimeException("Database error"))
                    .thenReturn(paymentOperation);
            when(paymentMapper.toPaymentResponse(eq(paymentOperation), eq("Database error")))
                    .thenReturn(new PaymentResponse(
                            PaymentStatus.FAILED,
                            0,
                            0L,
                            amount,
                            currency,
                            "Database error"
                    ));

            PaymentResponse response = paymentService.initiatePayment(paymentRequest);

            assertNotNull(response);
            assertEquals(PaymentStatus.FAILED, response.status());
            assertEquals("Database error", response.message());
            assertEquals(amount, response.amount());
            assertEquals(currency, response.currency());
            verify(paymentOperationRepository, times(2)).save(paymentOperation);
            verify(eventPublisher, never()).publishEvent(any(PaymentEvent.class));
            verify(paymentMapper, times(1)).toPaymentResponse(paymentOperation, "Database error");
        }
    }

    @Nested
    class CancelPaymentTest {

        @Test
        void givenValidData_whenCancelPayment_thenSuccess() {
            when(paymentOperationRepository.findById(paymentId)).thenReturn(Optional.of(paymentOperation));
            when(outboxEventRepository.notExistsSentAuth(paymentId, PaymentStatus.PENDING, OutboxStatus.SENT))
                    .thenReturn(false);
            when(paymentMapper.clone(paymentOperation)).thenReturn(paymentOperation);
            when(paymentOperationRepository.save(any(PaymentOperation.class))).thenReturn(paymentOperation);
            when(paymentMapper.toPaymentResponse(eq(paymentOperation), eq(""))).thenReturn(
                    new PaymentResponse(
                            PaymentStatus.CANCELED,
                            1234,
                            1001L,
                            amount,
                            currency,
                            ""
                    )
            );

            PaymentResponse response = paymentService.cancelPayment(paymentId);

            assertNotNull(response);
            assertEquals(PaymentStatus.CANCELED, response.status());
            assertEquals(amount, response.amount());
            assertEquals(currency, response.currency());
            assertEquals("", response.message());
            verify(paymentOperationRepository, times(2)).save(paymentOperation);
            verify(eventPublisher, times(1)).publishEvent(any(PaymentEvent.class));
            verify(paymentMapper, times(1)).toPaymentResponse(paymentOperation, "");
        }

        @Test
        void givenInvalidData_whenCancelPayment_thenPaymentNotFound() {
            when(paymentOperationRepository.findById(paymentId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> paymentService.cancelPayment(paymentId));
            assertEquals("Payment with id " + paymentId + " not found", exception.getMessage());
            verify(paymentOperationRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
            verify(paymentMapper, never()).toPaymentResponse(any(), any());
        }
    }

    @Nested
    class ForcedPaymentTest {

        @Test
        public void givenValidData_whenForcedPayment_thenSuccess() {
            when(paymentOperationRepository.findById(paymentId)).thenReturn(Optional.of(paymentOperation));
            when(outboxEventRepository.notExistsSentAuth(paymentId, PaymentStatus.PENDING, OutboxStatus.SENT))
                    .thenReturn(false);
            when(paymentMapper.clone(paymentOperation)).thenReturn(paymentOperation);
            when(paymentOperationRepository.save(any(PaymentOperation.class))).thenReturn(paymentOperation);
            when(paymentMapper.toPaymentResponse(eq(paymentOperation), eq(""))).thenReturn(
                    new PaymentResponse(
                            PaymentStatus.CLEARED,
                            1234,
                            1001L,
                            amount,
                            currency,
                            ""
                    )
            );

            PaymentResponse response = paymentService.forcedPayment(paymentId);

            assertNotNull(response);
            assertEquals(PaymentStatus.CLEARED, response.status());
            assertEquals(amount, response.amount());
            assertEquals(currency, response.currency());
            assertEquals("", response.message());
            verify(paymentOperationRepository, times(2)).save(paymentOperation);
            verify(eventPublisher, times(1)).publishEvent(any(PaymentEvent.class));
            verify(paymentMapper, times(1)).toPaymentResponse(paymentOperation, "");
        }

        @Test
        void givenValidData_whenForcedPayment_thenPaymentNotFound() {
            when(paymentOperationRepository.findById(paymentId)).thenReturn(Optional.empty());

            EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                    () -> paymentService.forcedPayment(paymentId));
            assertEquals("Payment with id " + paymentId + " not found", exception.getMessage());
            verify(paymentOperationRepository, never()).save(any());
            verify(eventPublisher, never()).publishEvent(any());
            verify(paymentMapper, never()).toPaymentResponse(any(), any());
        }
    }
}
