package faang.school.paymentservice.client.CurrencyConverter;

import faang.school.paymentservice.client.config.CurrencyConverterConfig;
import faang.school.paymentservice.dto.ExchangeRateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "currency-converter",
        url = "${currency-converter-api.url}",
        configuration = CurrencyConverterConfig.class
)
public interface CurrencyConverterClient {
    @GetMapping("/latest.json/")
    ExchangeRateDto getExchangeRates();
}
