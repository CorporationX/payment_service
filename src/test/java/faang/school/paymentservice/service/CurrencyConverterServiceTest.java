package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeServiceClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeResponse;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.service.currency.CurrencyConverterServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import faang.school.paymentservice.properties.ExchangeServiceProperties;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.math.RoundingMode;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CurrencyConverterServiceTest {
    @Mock
    private ExchangeServiceClient exchangeServiceClient;
    @Mock
    private ExchangeServiceProperties exchangeServiceProperties;
    @InjectMocks
    private CurrencyConverterServiceImpl currencyConverterService;
    @Mock
    private RedisService redisService;
    private Map<String, Double> rates;
    private PaymentRequest dto;

    @BeforeEach
    public void setUp() {
        redisService.delete("exchange-rates");
        prepareData();
        Mockito.lenient().when(exchangeServiceProperties.getToken()).thenReturn("token");
        Mockito.lenient().when(exchangeServiceProperties.getCommissionRate()).thenReturn(1.5);
    }

    @Test
    void testConvertCurrency() {


        ExchangeResponse response = new ExchangeResponse("disclaimer", "license", System.currentTimeMillis(), Currency.USD.name(), rates);

        when(exchangeServiceClient.exchange(any())).thenReturn(response);

        BigDecimal result = currencyConverterService.convertCurrency(dto);

        BigDecimal expectedRate = BigDecimal.valueOf(rates.get(dto.toCurrency().name()))
                .divide(BigDecimal.valueOf(rates.get(dto.fromCurrency().name())), RoundingMode.HALF_UP);
        BigDecimal expectedConvertedAmount = dto.amount().multiply(expectedRate);
        BigDecimal expectedCommission = expectedConvertedAmount.multiply(BigDecimal.valueOf(1.5))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal expectedAmount = expectedConvertedAmount.add(expectedCommission);

        assertNotNull(result);
        assertEquals(expectedAmount, result);
    }

    @Test
    void testConvertCurrencyRateNotExist() {
        rates.remove(dto.fromCurrency().name());

        ExchangeResponse response = new ExchangeResponse("disclaimer", "license", System.currentTimeMillis(), Currency.USD.name(), rates);

        when(exchangeServiceClient.exchange(any())).thenReturn(response);

        assertThrows(IllegalArgumentException.class, () -> {
            currencyConverterService.convertCurrency(dto);
        }, String.format("Exchange rate not found for specified currencies: %s to %s", dto.fromCurrency().name(), dto.toCurrency().name()));
    }

    private void prepareData() {

        dto = new PaymentRequest(UUID.randomUUID(), BigDecimal.valueOf(1000), Currency.USD, Currency.EUR);
        rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.85);
    }
}