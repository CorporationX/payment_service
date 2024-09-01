package faang.school.paymentservice.service;

import faang.school.paymentservice.cache.CurrencyRateCache;
import faang.school.paymentservice.dto.CurrencyRate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @author Evgenii Malkov
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateService {

    private final CurrencyRateCache rateCache;
    private final WebClient webClient;
    @Value("${currency.rate.fetch-url}")
    String fetchUrl;
    @Value("${currency.rate.access-key}")
    String accessKey;

    @Retryable(maxAttempts = 5, backoff = @Backoff(delay = 5000))
    public void fetchRates() {
        webClient
                .get()
                .uri(fetchUrl.concat("?access_key=").concat(accessKey))
                .retrieve()
                .bodyToMono(CurrencyRate.class)
                .doOnSuccess(this::updateRateCache)
                .block();
    }

    private void updateRateCache(CurrencyRate result) {
        rateCache.updateCache(result.getRates());
        log.info("Successful update currency rate cache, last timestamp: {}", result.getTimestamp());
    }
}
