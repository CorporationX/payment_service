package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.LatestRatesResponseDto;
import faang.school.paymentservice.exception.ExchangeServiceApiException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "openExchangeRatesClient",
        url = "${exchangerateapi.base-url}",
        configuration = FeignConfig.class)
public interface OpenExchangeRatesClient {
    @Retryable(
            value = {ExchangeServiceApiException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @GetMapping("/latest.json")
    LatestRatesResponseDto getLatestRates();
}
