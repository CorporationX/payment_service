package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;

import faang.school.paymentservice.dto.CurrencyRateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final CurrencyRateCache currencyRateCache;
    private final WebClient webClient;
    @Value("${currency-rate-fetcher.access_key}")
    private String accessKey;

    public void updateActualCurrencyRate() {
        String baseCurrency = currencyRateCache.getBaseCurrency();
        String symbols = Arrays.stream(Currency.values())
                .map(Enum::name)
                .filter(currency -> !currency.equals(baseCurrency))
                .collect(Collectors.joining(","));
        CurrencyRateDto rateDto = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("access_key", accessKey)
                        .queryParam("base", baseCurrency)
                        .queryParam("symbols", symbols)
                        .build()
                )
                .retrieve()
                .bodyToMono(CurrencyRateDto.class)
                .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(1)))
                .block();

        validateCurrencyRateResponse(rateDto);
        Map<Currency, Double> rates = rateDto.rates();
        currencyRateCache.setCurrencyRate(rates);
        log.info("Сохранён курс валют: " + rates);
    }

    public Map<Currency, Double> getAllCurrencyRates() {
        return currencyRateCache.getAllCurrencyRates();
    }

    private void validateCurrencyRateResponse(CurrencyRateDto rateDto) {
        if (rateDto == null) {
            String message = "Получен пустой ответ от сервиса курса валют.Обновление курса валют не выполнено";
            log.error(message);
            throw new NullPointerException(message);
        }

        if (rateDto.rates() == null) {
            String message = "Получен пустой список курса от сервиса курса валют.Обновление курса валют не выполнено";
            log.error(message);
            throw new NullPointerException(message);
        }
    }
}
