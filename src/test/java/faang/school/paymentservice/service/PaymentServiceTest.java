package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeCurrencyClient;
import faang.school.paymentservice.config.ExchangeCurrencyProperties;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRateResponse;
import faang.school.paymentservice.exception.ExchangeCurrencyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private ExchangeCurrencyClient currencyClient;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        ExchangeCurrencyProperties currencyProperties = new ExchangeCurrencyProperties(
                "https://", "av6bsn3", Currency.USD, 1.0);
        paymentService = new PaymentService(currencyClient, currencyProperties);

        CurrencyRateResponse response = CurrencyRateResponse.builder()
                .rates(Map.of("EUR", 0.96, "CAD", 1.418)).build();

        when(currencyClient.getCurrencyRate(currencyProperties.appId(), "USD")).thenReturn(response);
    }

    @Test
    void testConvertSuccess() {
        BigDecimal result = paymentService.convert(BigDecimal.valueOf(100.00), Currency.USD, Currency.EUR);
        BigDecimal shouldBe = BigDecimal.valueOf(96.96);

        assertEquals(shouldBe, result);
    }

    @Test
    void testConvertCurrencyNotFound() {

        assertThrows(ExchangeCurrencyException.class,
                () -> paymentService.convert(BigDecimal.valueOf(100.00), Currency.USD, Currency.BTC));
    }
}
