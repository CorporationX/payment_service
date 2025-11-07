package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.ExchangeRatesDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-rates",
        url = "${feign.currency-rates.url}")
public interface ExchangeRatesClient {
    @GetMapping
    ExchangeRatesDto getRates(@RequestParam("symbols") String requiredCurrency);
}
