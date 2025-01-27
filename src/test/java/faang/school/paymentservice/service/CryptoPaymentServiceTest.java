package faang.school.paymentservice.service;

import faang.school.paymentservice.client.UserServiceClient;
import faang.school.paymentservice.dto.order.OrderDto;
import faang.school.paymentservice.dto.payment.Currency;
import faang.school.paymentservice.dto.payment.PaymentRequest;
import faang.school.paymentservice.exception.PaymentSignatureInvalidException;
import faang.school.paymentservice.service.payment.impl.CryptoPaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CryptoPaymentServiceTest {
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private OrderService orderService;
    @InjectMocks
    private CryptoPaymentService paymentService;

    PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        paymentRequest = new PaymentRequest(
                1, BigDecimal.ONE, Currency.EUR
        );
    }

    @Test
    void testProcessPaymentWithInvalidSignature() {
        assertThrows(
                PaymentSignatureInvalidException.class,
                () -> paymentService.processPayment(paymentRequest, "invalid-key")
        );
    }

    @Test
    void testProcessPaymentSuccessCase() {
        when(orderService.updateOrderStatusToSuccess(anyLong()))
                .thenReturn(OrderDto.builder().id(1).build());
        paymentService.processPayment(paymentRequest, "crypto-signature");
        verify(orderService, atLeastOnce()).updateOrderStatusToSuccess(paymentRequest.getOrderId());
        verify(userServiceClient, atLeastOnce()).activatePremiumForUser(1L);
    }
}
