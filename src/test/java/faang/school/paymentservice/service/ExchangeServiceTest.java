package faang.school.paymentservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import faang.school.paymentservice.client.PaymentServiceClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeResp;
import faang.school.paymentservice.exception.CurrencyNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ExchangeServiceTest {

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private ExchangeService exchangeService;

    private final String appId = "testAppId";

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(exchangeService, "appId", appId);
    }

    @Test
    @DisplayName("Test convert is success")
    public void testConvert_Success() {
        Double amountInUSD = 100.0;
        Currency targetCurrency = Currency.EUR;
        Double exchangeRate = 0.85;

        Map<String, Double> rates = new HashMap<>();
        rates.put(targetCurrency.name(), exchangeRate);
        ExchangeResp exchangeResp = new ExchangeResp();
        exchangeResp.setRates(rates);

        Mockito.when(paymentServiceClient.getExchange(appId)).thenReturn(exchangeResp);

        Double result = exchangeService.convert(amountInUSD, targetCurrency);

        Double expectedAmount = amountInUSD * exchangeRate * 1.01;
        Assertions.assertEquals(expectedAmount, result);
        Mockito.verify(paymentServiceClient, Mockito.times(1))
                .getExchange(appId);
    }

    @Test
    @DisplayName("Test currency not found when currency not exist")
    public void testConvert_CurrencyNotFound() {
        Double amountInUSD = 100.0;
        Currency targetCurrency = Currency.USD;

        Map<String, Double> rates = new HashMap<>();
        ExchangeResp exchangeResp = new ExchangeResp();
        exchangeResp.setRates(rates);

        Mockito.when(paymentServiceClient.getExchange(appId)).thenReturn(exchangeResp);

        Assertions.assertThrows(CurrencyNotFoundException.class, () -> {
            exchangeService.convert(amountInUSD, targetCurrency);
        });
    }
}