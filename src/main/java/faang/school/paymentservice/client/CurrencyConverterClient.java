package faang.school.paymentservice.client;

import faang.school.paymentservice.response.CurrencyExchangeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currency-converter",url = "${currency.exchange.url}")
public interface CurrencyConverterClient {
    @GetMapping(value = "api/latest.json", produces = "application/json", consumes = "application/json")
    CurrencyExchangeResponse getCurrentCurrencyExchangeRate(@RequestParam(value = "app_id") String appId);
}

