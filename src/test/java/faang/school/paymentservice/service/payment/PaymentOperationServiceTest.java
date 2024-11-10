package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.payment.PaymentCreateDto;
import faang.school.paymentservice.dto.payment.PaymentDto;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.publisher.payment.PaymentEventPublisher;
import faang.school.paymentservice.service.exchangerate.CurrencyService;
import faang.school.paymentservice.validator.payment.PaymentValidator;
import faang.school.paymentservice.mapper.PaymentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentOperationServiceTest {

    private static final String USD = "USD";
    private static final String EUR = "EUR";
    private static final Currency BASE_CURRENCY_PAYMENT = Currency.USD;
    private static final UUID PAYMENT_ID = UUID.randomUUID();

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private PaymentValidator paymentValidator;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @Mock
    private PaymentStatusService paymentStatusUpdater;

    @Mock
    private PaymentService paymentService;

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private PaymentOperationService paymentOperationService;

    private Payment payment;
    private PaymentCreateDto paymentCreateDto;
    private PaymentDto paymentDto;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(PAYMENT_ID);
        payment.setStatus(PaymentStatus.AUTH_PENDING);

        paymentCreateDto = new PaymentCreateDto();
        paymentCreateDto.setAmount(BigDecimal.valueOf(100.00));
        paymentCreateDto.setSourceAccountId(UUID.randomUUID());
        paymentCreateDto.setTargetAccountId(UUID.randomUUID());
        paymentCreateDto.setClearScheduledAt(LocalDateTime.now());
        paymentCreateDto.setCurrency(USD);

        paymentDto = new PaymentDto();
        paymentDto.setId(PAYMENT_ID);
        paymentDto.setStatus(PaymentStatus.AUTH_PENDING.name());

        ReflectionTestUtils.setField(paymentOperationService, "baseCurrencyPayment", BASE_CURRENCY_PAYMENT);
    }

    @Test
    @DisplayName("Successfully sends payment")
    void whenSendPaymentThenReturnsPaymentDto() {
        when(paymentService.createAndPersistPayment(paymentCreateDto)).thenReturn(payment);
        when(paymentMapper.toPaymentDto(payment)).thenReturn(paymentDto);

        PaymentDto result = paymentOperationService.sendPayment(paymentCreateDto);

        verify(paymentValidator).validateIdempotencyKeyIsUnique(paymentCreateDto.getIdempotencyKey());
        verify(paymentValidator).validateDifferentAccounts(payment);
        verify(eventPublisher).publishPayment(payment);
        assertEquals(paymentDto, result);
    }

    @Test
    @DisplayName("Cancels payment and returns updated PaymentDto")
    void whenCancelPaymentThenReturnUpdatedPaymentDto() {
        when(paymentService.getPaymentById(PAYMENT_ID)).thenReturn(payment);
        when(paymentStatusUpdater.updatePaymentStatus(payment, PaymentStatus.CANCEL_PENDING)).thenReturn(payment);
        when(paymentMapper.toPaymentDto(payment)).thenReturn(paymentDto);

        PaymentDto result = paymentOperationService.cancelPayment(PAYMENT_ID);

        verify(paymentValidator).validateCancelableStatus(payment);
        verify(eventPublisher).publishPayment(payment);
        assertEquals(paymentDto, result);
    }

    @Test
    @DisplayName("Confirms payment and returns updated PaymentDto")
    void whenConfirmPaymentThenReturnUpdatedPaymentDto() {
        when(paymentService.getPaymentById(PAYMENT_ID)).thenReturn(payment);
        when(paymentStatusUpdater.updatePaymentStatus(payment, PaymentStatus.CONFIRM_PENDING)).thenReturn(payment);
        when(paymentMapper.toPaymentDto(payment)).thenReturn(paymentDto);

        PaymentDto result = paymentOperationService.confirmPayment(PAYMENT_ID);

        verify(paymentValidator).validateConfirmableStatus(payment);
        verify(eventPublisher).publishPayment(payment);
        assertEquals(paymentDto, result);
    }

    @Test
    @DisplayName("Processes clearing payments")
    void whenProcessClearingPaymentsThenUpdatesStatuses() {
        List<Payment> payments = List.of(payment);
        when(paymentService.getReadyForClearingPayments()).thenReturn(payments);
        when(paymentStatusUpdater.updatePaymentStatus(payment, PaymentStatus.CLEAR_PENDING)).thenReturn(payment);

        paymentOperationService.processClearingPayments();

        verify(paymentService).getReadyForClearingPayments();
        verify(paymentStatusUpdater).updatePaymentStatus(payment, PaymentStatus.CLEAR_PENDING);
        verify(eventPublisher).publishPayment(payment);
    }

    @Test
    @DisplayName("Adjusts currency to base if different")
    void whenCurrencyDifferentThenAdjustsToBaseCurrency() {
        paymentCreateDto.setCurrency(EUR);
        when(currencyService.convertCurrency(paymentCreateDto.getAmount(), Currency.EUR, BASE_CURRENCY_PAYMENT))
                .thenReturn(BigDecimal.valueOf(120.00));

        paymentOperationService.sendPayment(paymentCreateDto);

        assertEquals(BigDecimal.valueOf(120.00), paymentCreateDto.getAmount());
        assertEquals(BASE_CURRENCY_PAYMENT.name(), paymentCreateDto.getCurrency());
    }
}

