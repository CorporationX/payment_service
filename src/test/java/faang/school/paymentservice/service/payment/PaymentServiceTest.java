package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.client.account_service.AccountServiceClient;
import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.PaymentRepository;
import faang.school.paymentservice.service.payment.tools.IdempotenceKeyGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private IdempotenceKeyGenerator idempotenceKeyGenerator;

    @InjectMocks
    private PaymentService paymentService;

    private AccountDto accountFrom;
    private AccountDto accountTo;
    private Payment payment;
    private final String accountNumberFrom = "12345678901234567890";
    private final String accountNumberTo = "09876543210987654321";
    private final String idempotencyKey = "a1b2c3d4e5f67890123456789abcdef1234567890abcdef1234567890abcdef12";

    @BeforeEach
    void setUp() {
        accountFrom = new AccountDto();
        accountFrom.setId(UUID.randomUUID());
        accountFrom.setAccountNumber(accountNumberFrom);
        accountFrom.setCurrency(Currency.USD);
        accountFrom.setAccountStatus(AccountDto.AccountStatus.ACTIVE);

        accountTo = new AccountDto();
        accountTo.setId(UUID.randomUUID());
        accountTo.setAccountNumber(accountNumberTo);
        accountTo.setCurrency(Currency.USD);
        accountTo.setAccountStatus(AccountDto.AccountStatus.ACTIVE);

        payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100));
        payment.setCurrency(Currency.USD);
        payment.setStatus(PaymentStatus.AUTH_PENDING);
        payment.setClearScheduledAt(LocalDateTime.now().plusDays(1));
    }

    @Test
    void authorizePayment_Success() {
        when(accountServiceClient.getAccountByNumber(any(), eq(accountNumberFrom))).thenReturn(List.of(accountFrom));
        when(accountServiceClient.getAccountByNumber(any(), eq(accountNumberTo))).thenReturn(List.of(accountTo));
        when(idempotenceKeyGenerator.generateIdempotenceKey(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(idempotencyKey);
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        paymentService.authorizePayment(payment, accountNumberFrom, accountNumberTo);

        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void authorizePayment_IdempotencyKeyExistsWithinOneMinute_ThrowsException() {
        when(idempotenceKeyGenerator.generateIdempotenceKey(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(idempotencyKey);

        Payment existingPayment = new Payment();
        existingPayment.setIdempotencyKey(idempotencyKey);
        existingPayment.setCreatedAt(LocalDateTime.now());
        when(paymentRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingPayment));

        assertThrows(IllegalStateException.class,
                () -> paymentService.authorizePayment(payment, accountNumberFrom, accountNumberTo));
    }

    @Test
    void authorizePayment_IdempotencyKeyExistsOutsideOneMinute_GeneratesNewIdempotencyKey() {
        when(accountServiceClient.getAccountByNumber(any(), eq(accountNumberFrom))).thenReturn(List.of(accountFrom));
        when(accountServiceClient.getAccountByNumber(any(), eq(accountNumberTo))).thenReturn(List.of(accountTo));
        when(idempotenceKeyGenerator.generateIdempotenceKey(anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(idempotencyKey);

        Payment existingPayment = new Payment();
        existingPayment.setIdempotencyKey(idempotencyKey);
        existingPayment.setCreatedAt(LocalDateTime.now().minusMinutes(2));
        when(paymentRepository.findByIdempotencyKey(idempotencyKey)).thenReturn(Optional.of(existingPayment));

        String newIdempotencyKey = "newkey56789abcdef1234567890abcdef1234567890abcdef1234567890abcdef12";
        when(idempotenceKeyGenerator.generateIdempotenceKey(anyString(), anyString())).thenReturn(newIdempotencyKey);

        paymentService.authorizePayment(payment, accountNumberFrom, accountNumberTo);

        verify(paymentRepository, times(1)).save(existingPayment);
    }

    @Test
    void authorizePayment_FailureDueToInactiveAccount() {
        accountFrom.setAccountStatus(AccountDto.AccountStatus.SUSPENDED);
        when(accountServiceClient.getAccountByNumber(any(), eq(accountNumberFrom))).thenReturn(List.of(accountFrom));
        when(accountServiceClient.getAccountByNumber(any(), eq(accountNumberTo))).thenReturn(List.of(accountTo));

        assertThrows(IllegalStateException.class,
                () -> paymentService.authorizePayment(payment, accountNumberFrom, accountNumberTo));
    }

    @Test
    void authorizePayment_IncorrectCurrency() {
        accountFrom.setCurrency(Currency.RUB);
        when(accountServiceClient.getAccountByNumber(any(), eq(accountNumberFrom))).thenReturn(List.of(accountFrom));
        when(accountServiceClient.getAccountByNumber(any(), eq(accountNumberTo))).thenReturn(List.of(accountTo));

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.authorizePayment(payment, accountNumberFrom, accountNumberTo));
    }

    @Test
    void updatePaymentStatus_Success() {
        payment.setStatus(PaymentStatus.AUTH_SUCCESS);
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.SCHEDULED_PENDING);

        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void updatePaymentStatus_FailureDueToIncorrectStatusForUpdate() {
        assertThrows(IllegalArgumentException.class,
                () -> paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.CANCEL_SUCCESS));
    }

    @Test
    void updatePaymentStatus_FailureDueToIncorrectCurrentStatus() {
        payment.setStatus(PaymentStatus.AUTH_ERROR);
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        assertThrows(IllegalStateException.class,
                () -> paymentService.updatePaymentStatus(payment.getId(), PaymentStatus.SCHEDULED_PENDING));
    }

    @Test
    void updatePaymentStatusFromResponse_Success() {
        payment.setStatus(PaymentStatus.AUTH_PENDING);
        when(paymentRepository.findById(payment.getId())).thenReturn(Optional.of(payment));

        paymentService.updatePaymentStatusFromResponce(payment.getId(), PaymentStatus.AUTH_SUCCESS);

        verify(paymentRepository, times(1)).findById(payment.getId());
    }

    @Test
    void validateAmount_FailureWhenZeroOrNegative() {
        payment.setAmount(BigDecimal.ZERO);

        assertThrows(IllegalArgumentException.class,
                () -> paymentService.authorizePayment(payment, accountNumberFrom, accountNumberTo));
    }
}
