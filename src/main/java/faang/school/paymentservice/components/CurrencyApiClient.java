package faang.school.paymentservice.components;

import faang.school.paymentservice.dto.CurrencyResponse;
import reactor.core.publisher.Mono;

public interface CurrencyApiClient {
    Mono<CurrencyResponse> getCurrencyRates();
}
