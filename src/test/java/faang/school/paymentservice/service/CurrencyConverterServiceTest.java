package faang.school.paymentservice.service;

import faang.school.paymentservice.client.CurrencyClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import faang.school.paymentservice.dto.PaymentRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyConverterServiceTest {
    @InjectMocks
    CurrencyConverterService currencyConverterService;

    @Mock
    CurrencyClient currencyClient;

    private ExchangeRatesResponse response;
    private PaymentRequest paymentRequest = new PaymentRequest(1,BigDecimal.valueOf(1000),Currency.USD);

    @BeforeEach
    public void setUp() {
        currencyConverterService = new CurrencyConverterService(currencyClient, "dummy-id");
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.92539);
        response = new ExchangeRatesResponse("USD", rates);
    }

    @Nested
    class convert {
        @Test
        public void formInvalid() {
            response.rates().remove(Currency.USD.name());

            when(currencyClient.getRates(anyString())).thenReturn(response);
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                currencyConverterService.convert(paymentRequest, Currency.EUR);
            });
        }

        @Test
        public void toInvalid() {
            response.rates().remove(Currency.EUR.name());

            when(currencyClient.getRates(anyString())).thenReturn(response);
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                currencyConverterService.convert(paymentRequest, Currency.EUR);
            });
        }

        @Test
        void convertUSDToUSD() {
            when(currencyClient.getRates(anyString())).thenReturn(response);
            BigDecimal result = currencyConverterService.convert(paymentRequest, Currency.USD);
            assertEquals(new BigDecimal("1010.00"), result);
        }
        @Test
        void convertUSDToEUR() {
            when(currencyClient.getRates(anyString())).thenReturn(response);
            BigDecimal result = currencyConverterService.convert(paymentRequest, Currency.EUR);
            assertEquals(new BigDecimal("934.64"), result);
        }

        @Test
        void convertEURToUSD() {
            paymentRequest = new PaymentRequest(1,BigDecimal.valueOf(1000),Currency.EUR);

            when(currencyClient.getRates(anyString())).thenReturn(response);
            BigDecimal result = currencyConverterService.convert(paymentRequest, Currency.USD);
            assertEquals(new BigDecimal("1091.43"), result);
        }

        @Test
        void convertEURToEUR() {
            paymentRequest = new PaymentRequest(1,BigDecimal.valueOf(1000),Currency.EUR);

            when(currencyClient.getRates(anyString())).thenReturn(response);
            BigDecimal result = currencyConverterService.convert(paymentRequest, Currency.EUR);
            assertEquals(new BigDecimal("1010.00"), result);
        }
    }
}