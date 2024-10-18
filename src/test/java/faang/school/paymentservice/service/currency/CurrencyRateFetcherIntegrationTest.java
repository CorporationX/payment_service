package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.scheduler.CurrencyRateFetcher;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class CurrencyRateFetcherIntegrationTest {

    @InjectMocks
    private CurrencyRateFetcher currencyRateFetcher;

    @Mock
    private CurrencyService currencyService;

    @Test
    public void testFetchCurrencyRate() {
        doNothing().when(currencyService).updateCurrencyRates();
        currencyRateFetcher.fetchCurrencyRate();
        verify(currencyService, times(1)).updateCurrencyRates();
    }
}