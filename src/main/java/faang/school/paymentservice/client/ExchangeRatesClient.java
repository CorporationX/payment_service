package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.payment.ExchangeRates;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchangeRates", url = "${currency.exchange.url}")
public interface ExchangeRatesClient {

    @GetMapping("/v1/latest")
    ExchangeRates getExchangeRates(@RequestParam(name = "access_key") String accessKey,
                                   @RequestParam(name = "symbols") String actualCurrency);
}
