package faang.school.paymentservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyService {

    private final WebClient webClient;
    private Map<String, Double> exchangeRates = new ConcurrentHashMap<>();

    @Retryable(
            value = {WebClientResponseException.class, RuntimeException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void updateRates() {
        try {
            Map<String, Double> rates = webClient.get()
                    .uri("https://api.exchangeratesapi.io/latest")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            exchangeRates.clear();
            exchangeRates.putAll(rates);
            log.info("Курсы валют успешно обновлены: {}", exchangeRates);
        } catch (WebClientResponseException e) {
            log.error("Ошибка при получении данных о курсах валют: {}", e.getResponseBodyAsString(), e);
            throw e;
        } catch (RuntimeException e) {
            log.error("Ошибка при обновлении курсов валют", e);
            throw e;
        }
    }

    public Double getRate(String currencyCode) {
        return exchangeRates.get(currencyCode);
    }
}