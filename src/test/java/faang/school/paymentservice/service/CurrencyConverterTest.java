package faang.school.paymentservice.service;

import faang.school.paymentservice.model.enums.Currency;
import faang.school.paymentservice.model.dto.PaymentRequest;
import faang.school.paymentservice.exception.NotFoundException;
import faang.school.paymentservice.service.impl.CurrencyConverter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
@Component
class CurrencyConverterTest {

    @Mock
    private OpenExchangeRatesClient openExchangeRatesClient;

    @InjectMocks
    private CurrencyConverter currencyConverter;
    private BigDecimal amount;
    private String currencyRub;
    private String jsonAnswer;

    @BeforeEach
    void setUp() {
        amount = BigDecimal.TEN;
        currencyRub = "93.1098";
        jsonAnswer = "{\n" +
                "  \"disclaimer\": \"Usage subject to terms: https://openexchangerates.org/terms\",\n" +
                "  \"license\": \"https://openexchangerates.org/license\",\n" +
                "  \"timestamp\": 1727766000,\n" +
                "  \"base\": \"USD\",\n" +
                "  \"rates\": {\n" +
                "    \"AED\": 3.67295,\n" +
                "    \"AWG\": 1.8,\n" +
                "    \"AZN\": 1.7,\n" +
                "    \"RUB\": " + currencyRub + ",\n" +
                "    \"ZWL\": 322\n" +
                "  }\n" +
                "}";
    }

    @Test
    void getLatestExchangeRatesSuccess() {
        int commission = 1;
        BigDecimal amountAfterConvert = new BigDecimal(currencyRub).multiply(amount);
        BigDecimal amountAfterCommision = amountAfterConvert.divide(new BigDecimal(100))
                .multiply(new BigDecimal(commission)).add(amountAfterConvert);
        PaymentRequest request = new PaymentRequest(2L, amount, Currency.USD);
        when(openExchangeRatesClient.getLatest(null)).thenReturn(jsonAnswer);

        BigDecimal result = currencyConverter.getLatestExchangeRates(request, Currency.RUB);

        MathContext mathContext = new MathContext(4);
        BigDecimal expectedResult = amountAfterCommision.round(mathContext);
        result = result.round(mathContext);
        assertEquals(0, result.compareTo(expectedResult));
        verify(openExchangeRatesClient, times(1)).getLatest(null);
    }

    @Test
    void getLatestExchangeRatesSuccessFromRubToRub() {
        int commission = 1;
        BigDecimal amountAfterConvert = amount;
        BigDecimal amountAfterCommision = amountAfterConvert.divide(new BigDecimal(100))
                .multiply(new BigDecimal(commission)).add(amountAfterConvert);
        PaymentRequest request = new PaymentRequest(2L, amount, Currency.RUB);
        when(openExchangeRatesClient.getLatest(null)).thenReturn(jsonAnswer);

        BigDecimal result = currencyConverter.getLatestExchangeRates(request, Currency.RUB);

        MathContext mathContext = new MathContext(4);
        BigDecimal expectedResult = amountAfterCommision.round(mathContext);
        result = result.round(mathContext);
        assertEquals(0, result.compareTo(expectedResult));
        verify(openExchangeRatesClient, times(1)).getLatest(null);
    }

    @Test
    void getLatestExchangeUnSuccess() {
        String jsonAnswer = """
                {
                  "disclaimer": "Usage subject to terms: https://openexchangerates.org/terms",
                  "license": "https://openexchangerates.org/license",
                  "timestamp": 1727766000,
                  "base": "USD",
                  "rates": {
                    "AED": 3.67295,
                    "ZWL": 322
                  }
                }""";
        PaymentRequest request = new PaymentRequest(2L, amount, Currency.USD);
        when(openExchangeRatesClient.getLatest(null)).thenReturn(jsonAnswer);

        assertThrows(NotFoundException.class, () -> currencyConverter.getLatestExchangeRates(request, Currency.RUB));
    }
}