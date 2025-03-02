package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "open-exchange-rates",
        url = "${open_exchange_rates.url}",
        configuration = OpenExchangeRatesInterceptor.class)
public interface OpenExchangeRatesClient {
    @Retryable(retryFor = Exception.class,
            backoff = @Backoff(delay = 1000, multiplier = 2))
    @GetMapping("/latest.json")
    ExchangeRatesResponse getExchangeRates(@RequestParam("base") Currency paymentCurrency,
                                           @RequestParam("symbols") Currency targetCurrency);
}
