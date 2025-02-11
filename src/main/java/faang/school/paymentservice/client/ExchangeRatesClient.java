package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.payment.ExchangeRates;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "exchangeRates", url = "${currency.exchange.url}")
public interface ExchangeRatesClient {
    @GetMapping("/api/latest.json")
    ExchangeRates getExchangeRates(@RequestParam(name = "app_id") String appId,
                                   @RequestParam(required = false) String base,
                                   @RequestParam(required = false) List<String> symbols);
}
