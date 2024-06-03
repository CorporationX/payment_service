package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
public class CurrencyConverterTest {

    @InjectMocks
    private CurrencyConverter currencyConverter;

    @Mock
    ExchangeRatesClient exchangeRatesClient;

    private BigDecimal commission;
    private double usdRate;
    private double rubRate;
    private BigDecimal amount;
    private String appId;

    @BeforeEach
    public void setUp() {
        commission = BigDecimal.valueOf(1.01);
        usdRate = 1.0;
        rubRate = 90.0;
        amount = BigDecimal.valueOf(10);
        appId = "ca800d6c1f28496a9461bd842d20b919";
    }

    @Test
    public void testConverter() {
        currencyConverter.setCommission(commission);
        currencyConverter.setAppId(appId);

        Map<String, Double> rates = new HashMap<>();
        rates.put(Currency.USD.name(), usdRate);
        rates.put(Currency.RUB.name(), rubRate);

        ExchangeRatesResponse exchangeRatesResponse = new ExchangeRatesResponse();
        exchangeRatesResponse.setRates(rates);

        when(exchangeRatesClient.getRates(appId)).thenReturn(exchangeRatesResponse);

        BigDecimal expected = BigDecimal.valueOf(rubRate / usdRate).multiply(amount).multiply(commission);

        assertEquals(expected, currencyConverter.converter(Currency.USD, Currency.RUB, amount));
    }
}