package faang.school.paymentservice.config;

import faang.school.paymentservice.dto.CurrencyExchangeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currency-converter", url = "${url.service}")
public interface ConverterClient {
    @GetMapping(value = "api/latest.json", produces = "application/json", consumes = "application/json")
    CurrencyExchangeResponse getCurrentCurrencyExchangeRate(
            @RequestParam(value = "app_id") String appId,
            @RequestParam(value = "base") String base,
            @RequestParam(value = "symbols") String symbols);
}
