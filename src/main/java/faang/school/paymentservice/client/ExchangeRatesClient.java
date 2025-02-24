package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.payment.ExchangeRates;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

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
        URI url = UriComponentsBuilder.fromHttpUrl(exchangeUrl + "/v1/latest")
                .queryParam("access_key", accessKey)
                .queryParam("symbols", actualCurrency)
                .build()
                .toUri();

        return restTemplate.getForObject(url, ExchangeRates.class);
    }
}
