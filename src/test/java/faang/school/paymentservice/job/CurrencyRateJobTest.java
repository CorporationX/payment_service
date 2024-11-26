package faang.school.paymentservice.job;

import faang.school.paymentservice.service.currency.CurrencyService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CurrencyRateJobTest {

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencyRateJob currencyRateJob;

    @Test
    void updateCurrencyRatesTest() {

        currencyRateJob.updateCurrencyRates();

        verify(currencyService, times(1)).updateRates();
    }

   @Test
    void scheduledAnnotation_ShouldHaveCorrectCronExpression() throws NoSuchMethodException {
        String cron = currencyRateJob.getClass()
                .getMethod("updateCurrencyRates")
                .getAnnotation(Scheduled.class)
                .cron();

        assertEquals(cron, "${cron.update-rates}");
    }
}
