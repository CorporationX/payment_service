package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.currency.conversion.CurrencyConversionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {
    @Mock
    private CurrencyConversionService currencyConversionService;
    @InjectMocks
    private PaymentController paymentController;

    @BeforeEach
    public void setUp() {
        paymentController = new PaymentController(currencyConversionService);
    }

    @Test
    public void testSendPayment_VerifiesServiceCalledAndReturnsResponse() {

        PaymentRequest request = new PaymentRequest(123L, BigDecimal.valueOf(50.00), Currency.EUR);
        when(currencyConversionService.getConvertedSum(any())).thenReturn(BigDecimal.valueOf(100.00));

        PaymentResponse response = paymentController.sendPayment(request).getBody();

        verify(currencyConversionService).getConvertedSum(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, response.status());
        assertEquals(123L, response.paymentNumber());
        assertEquals(BigDecimal.valueOf(50.00), response.amount());
        assertEquals(Currency.EUR, response.currency());
        assertTrue(response.message().contains("Thank you for your purchase!"));
    }
}
