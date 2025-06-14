package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.client.OpenExchangeRatesClient;
import faang.school.paymentservice.dto.LatestRatesResponseDto;
import faang.school.paymentservice.exception.ExchangeRateException;
import faang.school.paymentservice.exception.ExchangeServiceApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyConverterServiceTest {
    @Mock
    private OpenExchangeRatesClient openExchangeRatesClient;
    @InjectMocks
    private CurrencyConverterService currencyConverterService;
    private String fromCurrency = "EUR";
    private BigDecimal amount = BigDecimal.valueOf(100);
    private LatestRatesResponseDto mockResponse;

    @BeforeEach
    void setUp() {
        mockResponse = LatestRatesResponseDto.builder()
                .disclaimer("Disclaimer")
                .license("License")
                .timestamp(System.currentTimeMillis() / 1000)
                .base("USD")
                .rates(Map.of(fromCurrency, BigDecimal.ONE))
                .build();
    }

    @Test
    void testSuccessConvertCurrencyFromEurToEur() {
        fromCurrency = "USD";
        assertEquals(BigDecimal.valueOf(100),
                currencyConverterService.convertCurrency(fromCurrency, amount));
    }

    @Test
    void testConvertCurrencySuccess() {
        BigDecimal rateFromUsdToFromCurrency = new BigDecimal("1.08");
        BigDecimal calculatedExchangeRateForUSD =
                BigDecimal.ONE.divide(rateFromUsdToFromCurrency, 10, RoundingMode.HALF_UP);
        mockResponse.setRates(Map.of(fromCurrency, rateFromUsdToFromCurrency));
        when(openExchangeRatesClient.getLatestRates()).thenReturn(mockResponse);

        int defaultFractionDigits = Currency.getInstance("USD").getDefaultFractionDigits();
        BigDecimal expectedResult = amount.multiply(calculatedExchangeRateForUSD)
                .setScale(defaultFractionDigits, RoundingMode.HALF_UP);

        assertEquals(expectedResult, currencyConverterService.convertCurrency(fromCurrency, amount));
        verify(openExchangeRatesClient, times(1)).getLatestRates();
    }

    @Test
    void testGetExchangeRateForUSDSuccess() {
        mockResponse.setRates( Map.of(fromCurrency, new BigDecimal("1.08")));
        when(openExchangeRatesClient.getLatestRates()).thenReturn(mockResponse);

        BigDecimal expectedRate = BigDecimal.ONE.divide(new BigDecimal("1.08"), 10, RoundingMode.HALF_UP);
        assertEquals(expectedRate, currencyConverterService.getExchangeRateForUSD(fromCurrency));
        verify(openExchangeRatesClient, times(1)).getLatestRates();
    }

    @Test
    void testGetExchangeRateForUSDRateNotFound() {
        mockResponse.setRates(Map.of(fromCurrency, BigDecimal.ZERO));
        when(openExchangeRatesClient.getLatestRates()).thenReturn(mockResponse);

        assertThrows(ExchangeRateException.class, () ->
                currencyConverterService.getExchangeRateForUSD(fromCurrency));
        verify(openExchangeRatesClient, times(1)).getLatestRates();
    }

    @Test
    void testGetExchangeRateForUSDInvalidRateNegative() {
        mockResponse.setRates(Map.of(fromCurrency, new BigDecimal("-0.5")));
        when(openExchangeRatesClient.getLatestRates()).thenReturn(mockResponse);

        assertThrows(ExchangeRateException.class, () ->
                currencyConverterService.getExchangeRateForUSD(fromCurrency));
        verify(openExchangeRatesClient, times(1)).getLatestRates();
    }

    @Test
    void testGetExchangeRateForUsdWhenRatesMapIsEmpty() {
        mockResponse.setRates(Map.of());
        when(openExchangeRatesClient.getLatestRates()).thenReturn(mockResponse);

        assertThrows(ExchangeServiceApiException.class, () ->
                currencyConverterService.getExchangeRateForUSD(fromCurrency));
        verify(openExchangeRatesClient, times(1)).getLatestRates();
    }
}