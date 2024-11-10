package faang.school.paymentservice.service.exchangerate;

import faang.school.paymentservice.client.CurrencyRatesClient;
import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.ExchangeRateResponseDto;
import faang.school.paymentservice.exception.ResponseDtoNotFoundException;
import faang.school.paymentservice.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @InjectMocks
    private CurrencyService currencyService;

    @Mock
    private ExchangeRatesClient exchangeRatesClient;

    @Mock
    private CurrencyRatesClient currencyRatesClient;

    @Mock
    private Map<String, Double> currencyRates;

    private static final String APP_ID = "appId";
    private static final BigDecimal AMOUNT = BigDecimal.valueOf(500);
    private static final BigDecimal RESULT_AMOUNT =
            BigDecimal.valueOf(466.1402500).setScale(7, RoundingMode.DOWN);
    private static final BigDecimal COMMISSION = BigDecimal.valueOf(0.01);
    private static final String DISCLAIMER = "disclaimer";
    private static final String LICENSE = "license";
    private static final long TIMESTAMP = 123456789L;
    private static final String BASE = "base";
    private static final Currency FROM_CURRENCY = Currency.USD;
    private static final Currency TO_CURRENCY = Currency.EUR;
    private static final Double EXCHANGE_RATE = 0.92305;
    private Map<String, Double> rates = new HashMap<>();
    private ExchangeRateResponseDto responseDto;

    @BeforeEach
    public void init() {
        ReflectionTestUtils.setField(currencyService, "appId", APP_ID);
        ReflectionTestUtils.setField(currencyService, "commission", COMMISSION);

        rates.put(TO_CURRENCY.name(), EXCHANGE_RATE);
        responseDto = ExchangeRateResponseDto.builder()
                .disclaimer(DISCLAIMER)
                .license(LICENSE)
                .timestamp(TIMESTAMP)
                .base(BASE)
                .rates(rates)
                .build();
    }

    @Test
    @DisplayName("Successful currency conversion")
    public void whenConvertCurrencyShouldSuccess() {
        when(exchangeRatesClient.getCurrentExchangeRates(APP_ID, FROM_CURRENCY, TO_CURRENCY))
                .thenReturn(Optional.of(responseDto));

        BigDecimal result = currencyService.convertCurrency(AMOUNT, FROM_CURRENCY, TO_CURRENCY);

        assertNotNull(result);
        assertEquals(RESULT_AMOUNT, result);
        verify(exchangeRatesClient).getCurrentExchangeRates(APP_ID, FROM_CURRENCY, TO_CURRENCY);
    }

    @Test
    @DisplayName("Exception if ExchangeRateResponseDto is null")
    public void whenConvertCurrencyWithResponseDtoIsNullThenThrowException() {
        when(exchangeRatesClient.getCurrentExchangeRates(APP_ID, FROM_CURRENCY, TO_CURRENCY))
                .thenReturn(Optional.empty());

        assertThrows(ResponseDtoNotFoundException.class,
                () -> currencyService.convertCurrency(AMOUNT, FROM_CURRENCY, TO_CURRENCY));
    }

    @Test
    @DisplayName("Exception if exchangeRate is null")
    public void whenConvertCurrencyWithExchangeRateIsNullThenThrowException() {
        responseDto.setRates(new HashMap<>());
        when(exchangeRatesClient.getCurrentExchangeRates(APP_ID, FROM_CURRENCY, TO_CURRENCY))
                .thenReturn(Optional.of(responseDto));

        assertThrows(IllegalArgumentException.class,
                () -> currencyService.convertCurrency(AMOUNT, FROM_CURRENCY, TO_CURRENCY));
    }
}