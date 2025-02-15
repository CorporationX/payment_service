package faang.school.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.CurrencyRateResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final CurrencyService currencyService;
    private final ObjectMapper objectMapper;
    private CurrencyRateResponseDto currencyRates;

    @Scheduled(initialDelayString = "${exchange-rates.scheduler.initialDelay}",
            fixedRateString = "${exchange-rates.scheduler.fixedRate}")
    public void getCurrencyExchangeRates() {
        Mono<String> response = currencyService.getCurrencyExchangeRates();

        response.subscribe(value -> {
            try {
                log.info("CurrencyRateFetcher#getCurrencyExchangeRates: response from external rate service is {}", value);
                currencyRates = objectMapper.readValue(value, CurrencyRateResponseDto.class);
                log.info("CurrencyRateFetcher#getCurrencyExchangeRates: following exchange rates were saved: {}", currencyRates.rates());
            } catch (JsonProcessingException e) {
                log.error("CurrencyRateFetcher#getCurrencyExchangeRates: method failed with message: {}", e.getMessage(), e);
                // todo создать exceptionHandler, создать кастомное исключение, обработать в exceptionHandler
                throw new RuntimeException(e);
            }
        });
    }
}
