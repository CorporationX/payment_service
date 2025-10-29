package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.ExchangeRatesDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-rates",
        url = "https://openexchangerates.org/api/latest.json?app_id=3d393b0fc31d4337a001c0fa1c34a37f")
public interface ExchangeRatesClient {
    @GetMapping
    ExchangeRatesDto getRates(@RequestParam("symbols") String requiredCurrency);
}
