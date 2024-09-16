package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.service.rates.CurrencyConverter;
import faang.school.paymentservice.service.rates.ExchangeRatesCash;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class CurrencyConverterTest {
    @Autowired
    private ExchangeRatesCash exchangeRatesCash;
    @Autowired
    private CurrencyConverter currencyConverter;

    @Test
    void convertTest() {
        BigDecimal amount = BigDecimal.valueOf(85.2);
        Double expectedEUR = 85.2;
        BigDecimal result = currencyConverter.convert(Currency.EUR, Currency.EUR, amount);
        assertEquals(expectedEUR, result.doubleValue());
    }
}
