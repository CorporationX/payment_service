package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.CurrencyRateResponse;
import reactor.core.publisher.Mono;

public interface CurrencyService {
    Mono<CurrencyRateResponse> getLatestRates();
}