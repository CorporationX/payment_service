package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.CurrencyExchangeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "currency-converter", url = "${currency.exchange.url}")
public interface ConverterClient {
    @GetMapping(value = "api/latest.json", produces = "application/json", consumes = "application/json")
    CurrencyExchangeResponse getCurrentCurrencyExchangeRate(@RequestParam(value = "app_id") String appId);
}
