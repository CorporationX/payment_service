package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.exchange.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "currencyClient")
public interface CurrencyClient {

    CurrencyResponse getCurrencyRates(String currency);
}
