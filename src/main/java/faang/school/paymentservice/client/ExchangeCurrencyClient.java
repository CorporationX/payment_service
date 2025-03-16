package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.CurrencyRateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient (name = "exchangeCurrency", url = "${currency.exchange.url}")
public interface ExchangeCurrencyClient {

    @GetMapping("/api/latest.json")
    CurrencyRateResponse getCurrencyRate(@RequestParam("app_id") String appId,
                                         @RequestParam("base") String base);

}
