package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.CurrencyRateResponse;
import reactor.core.publisher.Mono;

public interface CurrencyRateClient {
    Mono<CurrencyRateResponse> fetchLatestRates();
}