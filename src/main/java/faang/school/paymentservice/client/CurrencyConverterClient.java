package faang.school.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;

@FeignClient(name = "currency-converter",url = "${currency.fetch.url}")
public interface CurrencyConverterClient {
    @GetMapping(value = "{app_id}/latest/USD", produces = "application/json", consumes = "application/json")
    CurrencyExchangeResponse getCurrentCurrencyExchangeRate(@PathVariable(value = "app_id") String appId);
}