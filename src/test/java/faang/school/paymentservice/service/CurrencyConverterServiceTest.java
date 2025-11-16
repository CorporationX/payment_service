package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeClient;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import faang.school.paymentservice.exception.CurrencyConversionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyConverterServiceTest {

    @Mock
    private ExchangeClient exchangeClient;
    private CurrencyConverterService converterService;

    @BeforeEach
    void setUp() {
        converterService = new CurrencyConverterService(exchangeClient);

        ReflectionTestUtils.setField(converterService, "targetCurrency", "RUB");
        ReflectionTestUtils.setField(converterService, "commissionRate", BigDecimal.valueOf(0.05));
        ReflectionTestUtils.setField(converterService, "appId", "dummy_app_id");
    }

    @Test
    void convertToRub_givenValidRates_shouldReturnConvertedAmountWithCommission() {
        ExchangeRatesResponse dto = new ExchangeRatesResponse(
                "disc",
                "lic",
                123L,
                "USD",
                Map.of(
                        "USD", BigDecimal.valueOf(1.0),
                        "RUB", BigDecimal.valueOf(75.0)
                )
        );

        when(exchangeClient.getLatestRates("dummy_app_id")).thenReturn(dto);

        BigDecimal result = converterService.convertToRub("USD", BigDecimal.valueOf(100));

        assertEquals(BigDecimal.valueOf(7875.00).setScale(2), result);
    }

    @Test
    void convertToRub_givenMissingTargetRate_shouldThrowCurrencyConversionException() {
        ExchangeRatesResponse dto = new ExchangeRatesResponse(
                "disc",
                "lic",
                123L,
                "USD",
                Map.of("USD", BigDecimal.valueOf(1.0))
        );

        when(exchangeClient.getLatestRates("dummy_app_id")).thenReturn(dto);

        assertThrows(CurrencyConversionException.class, () ->
                converterService.convertToRub("USD", BigDecimal.valueOf(100))
        );
    }

    @Test
    void convertToRub_givenMissingSourceRate_shouldThrowCurrencyConversionException() {
        ExchangeRatesResponse dto = new ExchangeRatesResponse(
                "disc",
                "lic",
                123L,
                "USD",
                Map.of("RUB", BigDecimal.valueOf(75.0))
        );

        when(exchangeClient.getLatestRates("dummy_app_id")).thenReturn(dto);

        assertThrows(CurrencyConversionException.class, () ->
                converterService.convertToRub("USD", BigDecimal.valueOf(100))
        );
    }
}
