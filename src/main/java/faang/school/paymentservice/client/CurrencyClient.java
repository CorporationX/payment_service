package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.exchange.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.Optional;

@FeignClient(name = "currencyClient")
public interface CurrencyClient {

    Optional<CurrencyResponse> getCurrencyRates(String currency);
}
