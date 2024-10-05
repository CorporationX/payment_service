package faang.school.paymentservice.service;

import faang.school.paymentservice.client.OpenExchangesRatesClient;
import faang.school.paymentservice.config.currency.CurrencyExchangeParams;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import faang.school.paymentservice.exception.ClientResponseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyExchangeServiceTest {

    @InjectMocks
    private CurrencyExchangeServiceImpl service;

    @Mock
    private CurrencyExchangeParams exchangeParams;

    @Mock
    private OpenExchangesRatesClient ratesClient;

    private CurrencyExchangeResponse mockResponse;
    private long paymentNumber;
    private Currency currency;
    private Currency toCurrency;

    @BeforeEach
    public void setUp() {
        mockResponse = new CurrencyExchangeResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.85);
        rates.put("GBR", 0.75);
        mockResponse.setRates(rates);
        currency = Currency.USD;
        toCurrency = Currency.EUR;
        paymentNumber = 1L;
    }

    @Test
    public void testConvertWithCommission_Success() {
        when(ratesClient.getLatestRates("test-app-id")).thenReturn(mockResponse);
        when(exchangeParams.getAppId()).thenReturn("test-app-id");
        when(exchangeParams.getCommission()).thenReturn(0.01);

        PaymentRequest paymentRequest = new PaymentRequest(paymentNumber, BigDecimal.valueOf(100), currency);

        BigDecimal result = service.convertWithCommission(paymentRequest, toCurrency);
        BigDecimal roundedResult = result.setScale(2, RoundingMode.HALF_UP);

        assertThat(roundedResult).isEqualTo(BigDecimal.valueOf(85.85));
    }

    @Test
    public void testConvertWithCommission_WrongCurrency() {
        when(ratesClient.getLatestRates("test-app-id")).thenReturn(mockResponse);
        when(exchangeParams.getAppId()).thenReturn("test-app-id");

        PaymentRequest paymentRequest = new PaymentRequest(paymentNumber, BigDecimal.valueOf(100), toCurrency);

        assertThatThrownBy(() -> service.convertWithCommission(paymentRequest, Currency.JPY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Currency %s not supported"
                        .formatted(Currency.JPY.name()));
    }

    @Test
    public void testConvertWithCommission_FailedGetRates() {
        when(exchangeParams.getAppId()).thenReturn("test-app-id");
        when(exchangeParams.getAppId()).thenReturn("test-app-id").thenThrow(new ClientResponseException("Failed to get latest rates"));

        PaymentRequest paymentRequest = new PaymentRequest(paymentNumber, BigDecimal.valueOf(100), currency);

        assertThatThrownBy(() -> service.convertWithCommission(paymentRequest, toCurrency))
                .isInstanceOf(ClientResponseException.class)
                .hasMessage("Failed to get latest rates");
    }
}