package faang.school.paymentservice.service;

import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.exception.DuplicatePaymentException;
import faang.school.paymentservice.exception.PaymentOperationException;
import faang.school.paymentservice.repository.PaymentHashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentCheckServiceTest {

    @Mock
    private PaymentHashRepository paymentHashRepository;

    @InjectMocks
    private PaymentCheckService paymentCheckService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .id(UUID.randomUUID())
                .paymentStatus(PaymentStatus.NEW)
                .build();
    }

    @Test
    void testCheckNewPayment_WhenPaymentIsNew() {
        when(paymentHashRepository.isNewPayment(payment)).thenReturn(true);

        assertDoesNotThrow(() -> paymentCheckService.checkNewPayment(payment));
        verify(paymentHashRepository, times(1)).isNewPayment(payment);
    }

    @Test
    void testCheckNewPayment_WhenPaymentIsDuplicate() {
        when(paymentHashRepository.isNewPayment(payment)).thenReturn(false);

        assertThrows(DuplicatePaymentException.class, () -> paymentCheckService.checkNewPayment(payment));
        verify(paymentHashRepository, times(1)).isNewPayment(payment);
    }

    @Test
    void testAddNewPayment() {
        assertDoesNotThrow(() -> paymentCheckService.addNewPayment(payment));
        verify(paymentHashRepository, times(1)).insertPaymentHash(payment);
    }

    @Test
    void testCanAuthorizePayment_WhenPaymentIsNew() {
        when(paymentHashRepository.getAuthMessageCount(payment.getId())).thenReturn(0);

        assertTrue(paymentCheckService.canAuthorizePayment(payment));
        verify(paymentHashRepository, times(1)).getAuthMessageCount(payment.getId());
    }

    @Test
    void testCanAuthorizePayment_WhenPaymentIsNotNew() {
        payment.setPaymentStatus(PaymentStatus.AUTHORIZED);
        when(paymentHashRepository.getAuthMessageCount(payment.getId())).thenReturn(1);

        assertThrows(PaymentOperationException.class, () -> paymentCheckService.canAuthorizePayment(payment));
        verify(paymentHashRepository, times(1)).getAuthMessageCount(payment.getId());
    }

    @Test
    void testCanAuthorizePayment_WhenAuthMessageCountIsGreaterThanZero() {
        when(paymentHashRepository.getAuthMessageCount(payment.getId())).thenReturn(5);

        assertThrows(PaymentOperationException.class, () -> paymentCheckService.canAuthorizePayment(payment));
        verify(paymentHashRepository, times(1)).getAuthMessageCount(payment.getId());
    }

    @Test
    void testIncrementAuthorizationMessageCount() {
        assertDoesNotThrow(() -> paymentCheckService.incrementAuthorizationMessageCount(payment.getId()));
        verify(paymentHashRepository, times(1)).incrementAuthMessageCount(payment.getId());
    }

    @Test
    void testIsPaymentClearable_WhenPaymentIsClearable() {
        payment.setPaymentStatus(PaymentStatus.AUTHORIZED);
        when(paymentHashRepository.getClearingMessageCount(payment.getId())).thenReturn(0);

        assertTrue(paymentCheckService.isPaymentClearable(payment));
        verify(paymentHashRepository, times(1)).getClearingMessageCount(payment.getId());
    }

    @Test
    void testIsPaymentClearable_WhenPaymentIsNotClearable() {
        payment.setPaymentStatus(PaymentStatus.NEW);
        when(paymentHashRepository.getClearingMessageCount(payment.getId())).thenReturn(0);

        assertThrows(PaymentOperationException.class, () -> paymentCheckService.isPaymentClearable(payment));
        verify(paymentHashRepository, times(1)).getClearingMessageCount(payment.getId());
    }

    @Test
    void testIsPaymentClearable_WhenClearingMessageCountIsGreaterThanZero() {
        payment.setPaymentStatus(PaymentStatus.AUTHORIZED);
        when(paymentHashRepository.getClearingMessageCount(payment.getId())).thenReturn(5);

        assertThrows(PaymentOperationException.class, () -> paymentCheckService.isPaymentClearable(payment));
        verify(paymentHashRepository, times(1)).getClearingMessageCount(payment.getId());
    }

    @Test
    void testIncrementClearingMessageCount() {
        assertDoesNotThrow(() -> paymentCheckService.incrementClearingMessageCount(payment.getId()));
        verify(paymentHashRepository, times(1)).incrementClearingMessageCount(payment.getId());
    }

    @Test
    void testIsPaymentCancellable_WhenPaymentIsCancellable() {
        payment.setPaymentStatus(PaymentStatus.PROCESS_OF_CANCELLATION);
        when(paymentHashRepository.getCancelMessageCount(payment.getId())).thenReturn(0);

        assertTrue(paymentCheckService.isPaymentCancellable(payment));
        verify(paymentHashRepository, times(1)).getCancelMessageCount(payment.getId());
    }

    @Test
    void testIsPaymentCancellable_WhenPaymentIsNotCancellable() {
        payment.setPaymentStatus(PaymentStatus.NEW);
        when(paymentHashRepository.getCancelMessageCount(payment.getId())).thenReturn(0);

        assertThrows(PaymentOperationException.class, () -> paymentCheckService.isPaymentCancellable(payment));
        verify(paymentHashRepository, times(1)).getCancelMessageCount(payment.getId());
    }

    @Test
    void testIsPaymentCancellable_WhenCancelMessageCountIsGreaterThanZero() {
        payment.setPaymentStatus(PaymentStatus.PROCESS_OF_CANCELLATION);
        when(paymentHashRepository.getCancelMessageCount(payment.getId())).thenReturn(5);

        assertThrows(PaymentOperationException.class, () -> paymentCheckService.isPaymentCancellable(payment));
        verify(paymentHashRepository, times(1)).getCancelMessageCount(payment.getId());
    }

    @Test
    void testIncrementCancelMessageCount() {
        assertDoesNotThrow(() -> paymentCheckService.incrementCancelMessageCount(payment.getId()));
        verify(paymentHashRepository, times(1)).incrementCancelMessageCount(payment.getId());
    }

    @Test
    void testDeleteAuthorizationMessageCounter() {
        assertDoesNotThrow(() -> paymentCheckService.deleteAuthorizationMessageCounter(payment.getId()));
        verify(paymentHashRepository, times(1)).deleteAuthMessageCount(payment.getId());
    }

    @Test
    void testDeleteClearingMessageCounter() {
        assertDoesNotThrow(() -> paymentCheckService.deleteClearingMessageCounter(payment.getId()));
        verify(paymentHashRepository, times(1)).deleteClearingMessageCount(payment.getId());
    }

    @Test
    void testDeleteCancelMessageCounter() {
        assertDoesNotThrow(() -> paymentCheckService.deleteCancelMessageCounter(payment.getId()));
        verify(paymentHashRepository, times(1)).deleteCancelMessageCount(payment.getId());
    }
}