package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRate;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-service", url = "${services.exchange.url}")
public interface ExchangeServiceClient {

    @GetMapping("${services.exchange.rates-endpoint}")
    CurrencyRate getCurrencyRate(@RequestParam("app_id") String appId,
                                 @RequestParam("symbols") Currency to);
}
