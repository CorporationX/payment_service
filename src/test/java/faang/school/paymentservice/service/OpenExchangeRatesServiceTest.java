package faang.school.paymentservice.service;

import faang.school.paymentservice.client.OpenExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OpenExchangeRatesServiceTest {

    @InjectMocks
    private OpenExchangeRatesService openExchangeRatesService;

    @Mock
    private OpenExchangeRatesClient openExchangeRatesClient;

    @Test
    void testExchangeWhenRatesEmpty() {
        CurrencyResponse currencyResponseWithEmptyValues = new CurrencyResponse();
        when(openExchangeRatesClient.getRates(any(String.class), eq(Currency.USD), eq(List.of(Currency.EUR))))
                .thenReturn(currencyResponseWithEmptyValues);

        assertThrows(RuntimeException.class, () -> openExchangeRatesService.exchange(Currency.USD, Currency.EUR));
    }

    @Test
    void testExchangeWithRates() {
        CurrencyResponse currencyResponse = new CurrencyResponse();
        currencyResponse.setRates(Map.of(Currency.USD, 1.0D));
        BigDecimal expected = BigDecimal.valueOf(currencyResponse.getRates().get(Currency.USD));

        when(openExchangeRatesClient.getRates(any(), eq(Currency.EUR), eq(List.of(Currency.USD))))
                .thenReturn(currencyResponse);

        BigDecimal result = openExchangeRatesService.exchange(Currency.EUR, Currency.USD);

        assertEquals(expected, result);
    }
}