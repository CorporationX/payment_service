package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.exchange.CurrencyResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "currencyClient", url = "${services.currencyClient.url}")
public interface CurrencyClient {
    @GetMapping("/latest.json")
    CurrencyResponse getCurrencyRates(
            @RequestParam("app.id") String appId,
            @RequestParam("base") String baseCurrency,
            @RequestParam("symbols") String targetCurrency);
}
