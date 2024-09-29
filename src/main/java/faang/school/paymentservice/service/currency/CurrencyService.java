package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;

import faang.school.paymentservice.dto.CurrencyRateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {
    private final CurrencyUsdRateCache currencyUsdRateCache;
    private final WebClient webClient;
    @Value("${currency-rate-fetcher.access_key}")
    private String accessKey;
    @Value("${currency-rate-fetcher.base_currency}")
    private String baseCurrency;

    public void UpdateActualCurrencyRate() {
        String symbols = Arrays.stream(Currency.values())
                .map(Enum::name)
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
                .block();
        validateCurrencyRateResponse(rateDto);
        ConcurrentMap<Currency, Double> rates = new ConcurrentHashMap<>(rateDto.rates());
        currencyUsdRateCache.setCurrencyUsdRate(rates);
        log.info("Сохранён курс валют: \n" + rates);
    }

    public ConcurrentMap<Currency, Double> getCurrencyRates() {
        return currencyUsdRateCache.getAllCurrencyUsdRates();
    }

    private void validateCurrencyRateResponse(CurrencyRateDto rateDto) {
        if (rateDto == null) {
            String message = "Пришел пустой ответ от сервиса курса валют.Обновление курса валют не будет выполнено";
            log.error(message);
            throw new NullPointerException(message);
        }

        if (rateDto.rates() == null) {
            String message = "Пришел пустой список курса от сервиса курса валют.Обновление курса валют не будет выполнено";
            log.error(message);
            throw new NullPointerException(message);
        }
    }
}
