package faang.school.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    private final ExchangeRatesService exchangeRatesService;

    public Mono<String> getExchangeRates() {
        return exchangeRatesService.getExchangeRates();
    }
}