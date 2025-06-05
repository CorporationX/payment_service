package faang.school.paymentservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import faang.school.paymentservice.dto.ExchangeRateResponse;

@FeignClient(name = "openexchangerates", url = "${openexchagerates.url}", configuration = FeignConfig.class)
public interface OpenexchangeratesServiceClient {

    @GetMapping("/api/latest.json")
    public ExchangeRateResponse getExchangeRates(
        @RequestParam String app_id, 
        @RequestParam String symbols, 
        @RequestParam boolean prettyprint
    );
}
