package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ConverterClient;
import faang.school.paymentservice.config.CurrencyExchangeConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyExchangeResponse;
import faang.school.paymentservice.dto.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConverterServiceTest {

    @Mock
    private ConverterClient converterClient;

    @Mock
    private CurrencyExchangeConfig currencyExchangeConfig;

    @InjectMocks
    private ConverterService converterService;

    private CurrencyExchangeResponse dummyResponse;

    @BeforeEach
    void setUp() {
        dummyResponse = CurrencyExchangeResponse.builder()
                .timeStamp(123456789L)
                .base("EUR")
                .rates(Map.of("USD", 1.05, "EUR", 1.0))
                .build();

        lenient().when(currencyExchangeConfig.appId()).thenReturn("dummyAppId");
        lenient().when(currencyExchangeConfig.commission()).thenReturn(10.0);
    }


    @Test
    void testGetCurrentCurrencyExchangeRateSuccess() {
        when(converterClient.getCurrentCurrencyExchangeRate("dummyAppId")).thenReturn(dummyResponse);

        CurrencyExchangeResponse response = converterService.getCurrentCurrencyExchangeRate();

        assertNotNull(response, "Response не должен быть null");
        assertEquals("EUR", response.base(), "Базовая валюта должна быть EUR");
        assertFalse(response.rates().isEmpty(), "Курсы валют не должны быть пустыми");
    }

    @Test
    void testGetCurrentCurrencyExchangeRateFailure_NullResponse() {
        when(converterClient.getCurrentCurrencyExchangeRate("dummyAppId")).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> converterService.getCurrentCurrencyExchangeRate(),
                "Ожидается исключение при null ответе");
        assertTrue(exception.getMessage().contains("Не удалось получить корректные курсы валют."));
    }

    @Test
    void testConvertWithCommission_Success() {
        when(converterClient.getCurrentCurrencyExchangeRate("dummyAppId")).thenReturn(dummyResponse);

        PaymentRequest request = PaymentRequest.builder()
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.EUR)
                .build();

        BigDecimal expected = BigDecimal.valueOf(115.50).setScale(2, RoundingMode.HALF_UP);
        BigDecimal result = converterService.convertWithCommission(request, Currency.USD);

        assertEquals(0, expected.compareTo(result),
                "Конечная сумма с комиссией должна быть равна 115.50");
    }

    @Test
    void testConvertWithCommission_BaseRateZero() {
        CurrencyExchangeResponse responseWithZeroBase = new CurrencyExchangeResponse(
                123456789L,
                "EUR",
                Map.of("EUR", 0.0, "USD", 1.05));

        when(converterClient.getCurrentCurrencyExchangeRate("dummyAppId"))
                .thenReturn(responseWithZeroBase);


        PaymentRequest request = PaymentRequest.builder()
                .amount(BigDecimal.valueOf(100))
                .currency(Currency.EUR)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> converterService.convertWithCommission(request, Currency.USD),
                "Ожидается исключение, если базовый курс равен нулю");
        assertTrue(exception.getMessage().contains("Курс базовой валюты не может быть равен нулю."));
    }
}
