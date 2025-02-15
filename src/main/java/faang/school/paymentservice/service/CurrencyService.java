package faang.school.paymentservice.service;

import reactor.core.publisher.Mono;

public interface CurrencyService {

    Mono<String> getCurrencyExchangeRates();
}
