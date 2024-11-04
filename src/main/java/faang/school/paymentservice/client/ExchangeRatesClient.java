package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@FeignClient(name = "exchangeRatesClient", url = "${exchange.rates.url}")
public interface ExchangeRatesClient {

    @GetMapping("/latest.json")
    Optional<ExchangeRateResponseDto> getCurrentExchangeRates(@RequestParam("app_id") String appId,
                                                              @RequestParam("base") Currency baseCurrency,
                                                              @RequestParam("symbols") Currency targetCurrency);
}