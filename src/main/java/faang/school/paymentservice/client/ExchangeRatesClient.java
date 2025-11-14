package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.currency_converter.LatestExchangeRatesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-rates-service", url = "${openexchangerates.url}")
public interface ExchangeRatesClient {

    @GetMapping("/api/latest.json")
    LatestExchangeRatesResponse getLatestRates(@RequestParam("app_id") String appId);
}
