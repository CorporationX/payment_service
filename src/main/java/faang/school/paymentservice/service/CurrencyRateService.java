package faang.school.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateService {
    private final CurrencyService currencyService;
    private final Map<String, Double> latestRates = new ConcurrentHashMap<>();
    @Value("${currency.rates.fetch-cron}")
    private String cronExpression;

    @Scheduled(cron = "${currency.rates.fetch-cron}")
    public void fetchAndStoreRates() {
        log.info("Start update rate job (cron = {})", cronExpression);
        try {
            Map<String, Double> rates = currencyService.fetchLatestRates();
            latestRates.clear();
            latestRates.putAll(rates);
            log.info("Finished update rate job (cron = {})", cronExpression);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    // Allow to external services access to actual rates
    public Map<String, Double> getLatestRates() {
        return Collections.unmodifiableMap(latestRates);
    }
}
