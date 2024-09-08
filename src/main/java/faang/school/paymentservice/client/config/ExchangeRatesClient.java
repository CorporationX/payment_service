package faang.school.paymentservice.client.config;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "exchangeRates", url = "${feign_client.exchange_rates.url}")
public interface ExchangeRatesClient {

    @GetMapping("/api/latest.json")
    ExchangeRatesResponse getExchangeRates(@RequestParam(name = "app_id") String appId,
                                           @RequestParam(required = false) Currency base,
                                           @RequestParam(required = false) List<Currency> symbols);
}
