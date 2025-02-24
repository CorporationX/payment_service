package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.payment.ExchangeRates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class ExchangeRatesClient {
    private final RestTemplate restTemplate;

    @Value("${currency.exchange.url}")
    private String exchangeUrl;

    @Value("${currency.exchange.access-key}")
    private String accessKey;

    @Value("${currency.exchange.actual-currency}")
    private String actualCurrency;

    public ExchangeRates getExchangeRates() {
        String url = String.format("%s/v1/latest?access_key=%s&symbols=%s", exchangeUrl, accessKey, actualCurrency);

        return restTemplate.getForObject(url, ExchangeRates.class);
    }
}
