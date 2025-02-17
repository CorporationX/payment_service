package faang.school.paymentservice.service;

import faang.school.paymentservice.config.ExchangeRateConfig;
import faang.school.paymentservice.dto.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final ExchangeRateConfig config;
    private final WebClient webClient;

    @Retryable(
            retryFor = {Exception.class},
            maxAttemptsExpression = "#{${currency.exchangerate.retry.maxAttempts}}",
            backoff = @Backoff(delayExpression = "#{${currency.exchangerate.retry.delay}}", multiplier = 2)
    )
    public ExchangeRateResponse fetchLatestRates(String currencies) {
        String url = String.format("%s/latest?access_key=%s&symbols=%s&format=1",
                config.getUrl(),
                config.getApiKey(),
                currencies);
        log.info("Запрос обменных курсов по URL: {}", url);
        try {
            ExchangeRateResponse response = webClient.get().uri(url).retrieve()
                    .bodyToMono(ExchangeRateResponse.class).block();
            if (response == null || !response.success()) {
                throw new RuntimeException("Ошибка при получении данных курсов: " + response);
            }
            log.info("Получены курсы валют: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Ошибка при обращении к API обменных курсов", e);
            throw e;
        }
    }
}