package faang.school.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "exchange-rates", url = "https://openexchangerates.org/api")
public class ExchangeRatesClient {
    @GetMapping("/latest.json")
    ExchangeRatesResponse getRates(@RequestParam("app_id") String appId);
}
