package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.ExchangeRatesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currencyClient", url = "${currency.api-url}")
public interface CurrencyClient {

    @GetMapping("${currency.latest-path}")
    ExchangeRatesResponse getRates(@RequestParam("app_id") String appId);
}
