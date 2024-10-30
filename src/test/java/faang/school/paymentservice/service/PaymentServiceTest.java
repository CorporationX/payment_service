package faang.school.paymentservice.service;

import faang.school.paymentservice.client.account_service.AccountServiceClient;
import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static faang.school.paymentservice.dto.account.QueryType.NUMBER;
import static faang.school.paymentservice.model.PaymentStatus.AUTH;
import static faang.school.paymentservice.model.PaymentStatus.FORCED;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @InjectMocks
    private PaymentService paymentService;

    private AccountDto accountFrom;
    private AccountDto accountTo;

    private final String validAccountNumberFrom = "12345678901234567890";
    private final String validAccountNumberTo = "09876543210987654321";

    @BeforeEach
    void setUp() {
        accountFrom = new AccountDto();
        accountFrom.setId(UUID.randomUUID());
        accountFrom.setCurrency(Currency.USD);

        accountTo = new AccountDto();
        accountTo.setId(UUID.randomUUID());
        accountTo.setCurrency(Currency.USD);
    }

    @Test
    void testAuthorizePayment_Success() {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency(Currency.USD);

        when(accountServiceClient.getAccountByNumber(NUMBER, validAccountNumberFrom))
                .thenReturn(List.of(accountFrom));
        when(accountServiceClient.getAccountByNumber(NUMBER, validAccountNumberTo))
                .thenReturn(List.of(accountTo));

        paymentService.authorizePayment(payment, validAccountNumberFrom, validAccountNumberTo);

        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testAuthorizePayment_InvalidAmount() {
        Payment payment = new Payment();
        payment.setAmount(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class, () ->
                paymentService.authorizePayment(payment, validAccountNumberFrom, validAccountNumberTo));

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void testAuthorizePayment_CurrencyMismatch() {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency(Currency.EUR);

        accountFrom.setCurrency(Currency.USD);
        accountTo.setCurrency(Currency.USD);

        when(accountServiceClient.getAccountByNumber(any(), eq(validAccountNumberFrom)))
                .thenReturn(List.of(accountFrom));
        when(accountServiceClient.getAccountByNumber(any(), eq(validAccountNumberTo)))
                .thenReturn(List.of(accountTo));

        assertThrows(IllegalArgumentException.class, () ->
                paymentService.authorizePayment(payment, validAccountNumberFrom, validAccountNumberTo));

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void testAuthorizePayment_AccountNotFound() {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCurrency(Currency.USD);

        when(accountServiceClient.getAccountByNumber(any(), eq(validAccountNumberFrom))).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () ->
                paymentService.authorizePayment(payment, validAccountNumberFrom, validAccountNumberTo));

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void testChangePaymentStatus_Success() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(AUTH);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        paymentService.changePaymentStatus(paymentId, FORCED);

        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void testChangePaymentStatus_InvalidStatus() {
        UUID paymentId = UUID.randomUUID();
        Payment payment = new Payment();
        payment.setId(paymentId);
        payment.setStatus(FORCED);

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        assertThrows(IllegalStateException.class, () ->
                paymentService.changePaymentStatus(paymentId, FORCED));
    }

    @Test
    void testChangePaymentStatus_PaymentNotFound() {
        UUID paymentId = UUID.randomUUID();
        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                paymentService.changePaymentStatus(paymentId, FORCED));
    }

    @Test
    void testAuthorizePayment_DifferentAccountCurrencies() {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("200.00"));
        payment.setCurrency(Currency.EUR);

        accountFrom.setCurrency(Currency.USD);
        accountTo.setCurrency(Currency.USD);

        when(accountServiceClient.getAccountByNumber(any(), eq(validAccountNumberFrom)))
                .thenReturn(List.of(accountFrom));
        when(accountServiceClient.getAccountByNumber(any(), eq(validAccountNumberTo)))
                .thenReturn(List.of(accountTo));

        assertThrows(IllegalArgumentException.class, () ->
                paymentService.authorizePayment(payment, validAccountNumberFrom, validAccountNumberTo));

        verify(paymentRepository, never()).save(any());
    }

    @Test
    void testAuthorizePayment_AccountToNotFound() {
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("150.00"));
        payment.setCurrency(Currency.USD);

        when(accountServiceClient.getAccountByNumber(any(), eq(validAccountNumberFrom))).thenReturn(List.of(accountFrom));
        when(accountServiceClient.getAccountByNumber(any(), eq(validAccountNumberTo))).thenReturn(Collections.emptyList());

        assertThrows(IllegalArgumentException.class, () ->
                paymentService.authorizePayment(payment, validAccountNumberFrom, validAccountNumberTo));

        verify(paymentRepository, never()).save(any());
    }
}