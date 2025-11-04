package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.ExchangeRatesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Component
@FeignClient(
        name = "exchangeRates",
        url = "${external.exchangeratesapi.base-url}"
)
public interface ExternalCurrencyClient {

    @GetMapping("/latest/{base}")
    ExchangeRatesResponse fetchLatestRates(@PathVariable("base") String base);
}
