package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.ExchangeRatesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchangeClient", url = "${openexchangerates.url}")
public interface ExchangeClient {

    @GetMapping("/api/latest.json")
    ExchangeRatesResponse getLatestRates(@RequestParam("app_id") String appId);
}
