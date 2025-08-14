package faang.school.paymentservice.client.currency;

import faang.school.paymentservice.dto.CurrencyRateDto;
import reactor.core.publisher.Mono;

public interface CurrencyClient {
    Mono<CurrencyRateDto> getCurrencyRates();
}
