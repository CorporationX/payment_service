package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "currency-service", url = "https://openexchangerates.org")
public interface OpenExchangeRatesClient {
    @GetMapping("/api/latest.json")
    CurrencyResponse getRates(
            @RequestParam(name = "app_id") String appId,
            @RequestParam(required = false) Currency base,
            @RequestParam(required = false) List<Currency> symbols
    );
}
