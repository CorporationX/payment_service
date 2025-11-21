package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentServiceImplTest {

    private CurrencyConverterServiceImpl converterService;
    private ThreadLocal<DecimalFormat> formatter;
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        converterService = mock(CurrencyConverterServiceImpl.class);
        formatter = ThreadLocal.withInitial(() -> new DecimalFormat("0.00"));
        paymentService = new PaymentServiceImpl(converterService, formatter);

        ReflectionTestUtils.setField(paymentService, "targetCurrency", "RUB");
        ReflectionTestUtils.setField(paymentService, "verificationCodeMin", 100);
        ReflectionTestUtils.setField(paymentService, "verificationCodeMax", 10000);
    }

    @Test
    void processPayment_givenValidRequest_shouldReturnPaymentResponseWithConvertedAmountAndRUB() {
        PaymentRequest request = new PaymentRequest(12345L, BigDecimal.valueOf(100), Currency.USD);

        when(converterService.convertToTargetCurrency("USD", BigDecimal.valueOf(100)))
                .thenReturn(BigDecimal.valueOf(7500));

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals(PaymentStatus.SUCCESS, response.status());
        assertEquals(BigDecimal.valueOf(7500), response.amount());
        assertEquals("RUB", response.currency().name());
        assertTrue(response.verificationCode() >= 100 && response.verificationCode() < 10000);
        assertTrue(response.message().contains("7500"));
    }
}