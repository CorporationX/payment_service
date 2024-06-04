package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-rates", url = "https://openexchangerates.org/api")
public interface ExchangeRatesClient {

    @GetMapping("/latest.json")
    ExchangeRatesResponse getRates(@RequestParam("app_id") String appId);
}