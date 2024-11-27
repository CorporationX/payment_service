package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.currency.CurrencyService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CurrencyRateFetcherTest {

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencyRateFetcher currencyRateFetcher;
}
