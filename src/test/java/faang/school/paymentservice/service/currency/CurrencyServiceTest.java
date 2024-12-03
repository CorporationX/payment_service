package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.service.currency.rates.ExchangeRatesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {

    @Mock
    private ExchangeRatesService exchangeRatesService;

    @InjectMocks
    private CurrencyService currencyService;

    @Test
    void testSuccessfulAttemptToGetExchangeRates() {
        Mono<String> expectedResult = Mono.just("USD: 1.23");
        when(exchangeRatesService.getExchangeRates()).thenReturn("USD: 1.23");

        String actualResult = currencyService.getExchangeRates();

        assertEquals(expectedResult.block(), actualResult);
    }
}
