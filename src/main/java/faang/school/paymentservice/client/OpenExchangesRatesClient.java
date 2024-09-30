package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "open-exchanges-rates", url = "${currency.exchange.url}")
public interface OpenExchangesRatesClient {

    @GetMapping()
    CurrencyExchangeResponse getLatestRates(@RequestParam("app_id") String appId);
}