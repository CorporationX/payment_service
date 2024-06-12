package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRatesClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {

    private final ExchangeRatesClient exchangeRatesClient;
    private final RatesCache ratesCache;

    @Value("${services.openexchangerates.app_id}")
    private String appId;

    @Scheduled(cron = "${services.currencyRateFetcher.cron}")
    public void addActualRatesInCache() {
        ratesCache.getRates().clear();
        ratesCache.setRates(getActualRates());
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 10, backoff = @Backoff(delay = 1000, multiplier = 2))
    private Map<String, Double> getActualRates() {
        return exchangeRatesClient.getRates(appId).getRates();
    }
}