package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.currency_converter.LatestExchangeRatesResponse;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.exception.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {
    @InjectMocks
    private PaymentServiceImpl paymentService;
    @Mock
    private ExchangeRatesClient exchangeRatesClient;

    private final BigDecimal amount = new BigDecimal("87.02");
    private LatestExchangeRatesResponse mockRates;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(paymentService, "appId", "test-app-id");
        ReflectionTestUtils.setField(paymentService, "commissionRate", new BigDecimal("0.01"));
        ReflectionTestUtils.setField(paymentService, "defaultCurrency", Currency.USD);
        ReflectionTestUtils.setField(paymentService, "roundingConvertedAmount", 6);
        ReflectionTestUtils.setField(paymentService, "roundingFinalAmount", 2);
        ReflectionTestUtils.setField(paymentService, "lowerLimitVerificationCode", 1000);
        ReflectionTestUtils.setField(paymentService, "upperLimitVerificationCode", 10000);

        Map<String, BigDecimal> ratesMap = Map.of(
                "USD", new BigDecimal("1"),
                "EUR", new BigDecimal("0.871025")
        );
        mockRates = new LatestExchangeRatesResponse(
                "Usage subject to terms: https://openexchangerates.org/terms",
                "https://openexchangerates.org/license",
                1762167600L,
                "USD",
                ratesMap
        );
    }

    @Test
    void testConvert_EurToUsd_Success() {
        when(exchangeRatesClient.getLatestRates("test-app-id")).thenReturn(mockRates);

        BigDecimal result = paymentService.convertCurrency(amount, Currency.EUR, Currency.USD);

        // 87.02 EUR -> 87.02 / 0.871025 = 99.91 USD -> +1% = 100.90 USD
        BigDecimal expected = new BigDecimal("100.90").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expected, result);
    }

    @Test
    void testConvert_SameCurrency_NoConversion() {
        BigDecimal result = paymentService.convertCurrency(amount, Currency.USD, Currency.USD);

        assertEquals(amount, result);
        verifyNoInteractions(exchangeRatesClient);
    }

    @Test
    void testConvert_RateNotFound_ThrowsException() {
        when(exchangeRatesClient.getLatestRates("test-app-id")).thenReturn(mockRates);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.convertCurrency(amount, Currency.RUB, Currency.USD)
        );
        assertTrue(exception.getMessage().contains("RUB"));
        verify(exchangeRatesClient).getLatestRates(eq("test-app-id"));
    }

    @Test
    void testConvert_ApiError_ThrowsDataValidationException() {
        when(exchangeRatesClient.getLatestRates("test-app-id"))
                .thenThrow(new RuntimeException("API Error"));

        assertThrows(DataValidationException.class, () ->
                paymentService.convertCurrency(amount, Currency.EUR, Currency.USD));
        verify(exchangeRatesClient).getLatestRates(eq("test-app-id"));
    }

    @Test
    void testSendPayment_Success() {
        when(exchangeRatesClient.getLatestRates(anyString())).thenReturn(mockRates);

        PaymentRequest request = new PaymentRequest(12345L, amount, Currency.EUR);

        PaymentResponse response = paymentService.sendPayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, response.status());
        assertEquals(12345L, response.paymentNumber());
        assertEquals(Currency.EUR, response.currency());
        assertEquals("Dear friend! Thank you for your purchase! Your payment on 87,02 EUR was accepted.", response.message());

        // Проверка суммы: 87.02 EUR → 99.91 USD → +1% → 100.90 USD
        BigDecimal expectedAmount = new BigDecimal("100.90").setScale(2, RoundingMode.HALF_UP);
        assertEquals(expectedAmount, response.amount());

        verify(exchangeRatesClient).getLatestRates(eq("test-app-id"));
    }

    @Test
    void testSendPayment_SameCurrency_NoConversion() {
        PaymentRequest request = new PaymentRequest(67890L, new BigDecimal("50.00"), Currency.USD);

        PaymentResponse response = paymentService.sendPayment(request);

        assertEquals(new BigDecimal("50.00"), response.amount());
        assertEquals(Currency.USD, response.currency());
        verifyNoInteractions(exchangeRatesClient);
    }

    @Test
    void testSendPayment_RateNotFound_ThrowsException() {
        PaymentRequest request = new PaymentRequest(11111L, new BigDecimal("100.00"), Currency.RUB);
        when(exchangeRatesClient.getLatestRates(anyString())).thenReturn(mockRates);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.sendPayment(request)
        );
        assertTrue(exception.getMessage().contains("RUB"));
        verify(exchangeRatesClient).getLatestRates(eq("test-app-id"));
    }
}