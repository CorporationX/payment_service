package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.ExchangeRates;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-rates-service", url = "${api.openexchangerates.base-url}")
public interface ExchangeRatesClient {

    @GetMapping("/api/latest.json")
    ExchangeRates getExchangeRates(@RequestParam("app_id") String appId);
}
