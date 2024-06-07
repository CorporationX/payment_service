package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import faang.school.paymentservice.validator.CurrencyValidator;
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

    @Mock
    CurrencyValidator currencyValidator;

    private BigDecimal commission;
    private double usdRate;
    private double rubRate;
    private BigDecimal amount;
    private String appId;
    private Currency fromCurrency;
    private Currency toCurrency;

    @BeforeEach
    public void setUp() {
        commission = BigDecimal.valueOf(1.01);
        usdRate = 1.0;
        rubRate = 90.0;
        amount = BigDecimal.valueOf(10);
        appId = "ca800d6c1f28496a9461bd842d20b919";
        fromCurrency = Currency.USD;
        toCurrency = Currency.RUB;
    }

    @Test
    public void testConverter() {
        currencyConverter.setCommission(commission);
        currencyConverter.setAppId(appId);

        Map<String, Double> rates = new HashMap<>();
        rates.put(fromCurrency.name(), usdRate);
        rates.put(toCurrency.name(), rubRate);

        ExchangeRatesResponse exchangeRatesResponse = new ExchangeRatesResponse();
        exchangeRatesResponse.setRates(rates);

        when(exchangeRatesClient.getRates(appId)).thenReturn(exchangeRatesResponse);

        BigDecimal expected = BigDecimal.valueOf(rubRate / usdRate).multiply(amount).multiply(commission);

        assertEquals(expected, currencyConverter.converter(fromCurrency, toCurrency, amount));
    }
}