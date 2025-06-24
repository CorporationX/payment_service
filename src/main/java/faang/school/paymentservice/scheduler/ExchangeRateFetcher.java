package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.exception.CurrencyConversionException;
import faang.school.paymentservice.service.currency.conversion.CurrencyConversionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
@Slf4j
public class ExchangeRateFetcher {
    private final CurrencyConversionService currencyConversionService;

    @Scheduled(cron = "${currency-converter-api.refresh-cron}")
    public void scheduledRefreshRates() {
        try {
            currencyConversionService.getExchangeRates();
            log.info("Exchange rates successfully refreshed");
        } catch (CurrencyConversionException e) {
            log.warn("Failed to refresh exchange rates: {}", e.getMessage());
        }
    }

}
