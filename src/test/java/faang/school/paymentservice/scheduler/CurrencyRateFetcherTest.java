package faang.school.paymentservice.scheduler;

import static org.junit.jupiter.api.Assertions.*;

import faang.school.paymentservice.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.annotation.Scheduled;

import static org.mockito.Mockito.*;

class CurrencyRateFetcherTest {

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencyRateFetcher currencyRateFetcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Scheduled(cron = "${currency.fetch.cron}")
    void fetchRates_shouldCallUpdateRates() {
        // Act
        currencyRateFetcher.fetchRates();

        // Assert
        verify(currencyService, times(1)).updateRates();
    }
}