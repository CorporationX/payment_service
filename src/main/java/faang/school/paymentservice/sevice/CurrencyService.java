package faang.school.paymentservice.sevice;

import faang.school.paymentservice.client.currency.CurrencyClient;
import faang.school.paymentservice.dto.CurrencyRateDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final CurrencyClient currencyClient;

    @CachePut(value = "exchangeRates", key = "#result.base", condition = "#result.base != null")
    public CurrencyRateDto fetchCurrencyRates() {
        Mono<CurrencyRateDto> currencyRates = currencyClient.getCurrencyRates();
        CurrencyRateDto response = currencyRates
                .doFinally(signal -> log.info("Currency rates fetch completed. Cause {}", signal.name()))
                .block();

        if (null == response.base()) {
            log.warn("Base currency is null, skipping cache update");
            return response;
        }

        log.info("Currency rates fetched. Put to cache: key={} value={}", response.base(), response);
        return response;
    }
}
