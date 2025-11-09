package faang.school.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(name = "exchangeClient", url = "${openexchangerates.url}")
public interface ExchangeClient {

    @GetMapping("/api/latest.json")
    Map<String, Object> getLatestRates(@RequestParam("app_id") String appId);
}
