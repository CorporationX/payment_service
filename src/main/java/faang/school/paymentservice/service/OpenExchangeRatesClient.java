package faang.school.paymentservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "open-exchange-rates-service",
        url = "${services.openexchange.url}")
public interface OpenExchangeRatesClient {
    @GetMapping("/api/latest.json?app_id={appId}")
    String getLatest(@PathVariable String appId);
}