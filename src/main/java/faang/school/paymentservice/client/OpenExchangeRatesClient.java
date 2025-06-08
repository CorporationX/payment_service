package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.LatestRatesResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "openExchangeRatesClient",
        url = "${exchangerateapi.base-url}",
        configuration = FeignConfig.class)
public interface OpenExchangeRatesClient {
    @GetMapping("/latest.json")
    LatestRatesResponseDto getLatestRates();
}
