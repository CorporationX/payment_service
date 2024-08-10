package faang.school.paymentservice.client;

import faang.school.paymentservice.config.currencyClient.OpenExchangeCurrencyConfig;
import faang.school.paymentservice.dto.exchange.CurrencyResponse;
import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "openExchangeClient",
        url = "${services.currencyClient.url}",
        configuration = OpenExchangeCurrencyConfig.class)
public interface OpenExchangeClient extends CurrencyClient {
    @Override
    @GetMapping("/latest.json")
    @Retryable(retryFor = {FeignException.class}, backoff = @Backoff(delay = 1000, multiplier = 2))
    Optional<CurrencyResponse> getCurrencyRates(@RequestParam("symbols") String targetCurrency);
}
