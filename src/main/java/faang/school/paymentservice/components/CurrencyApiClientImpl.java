package faang.school.paymentservice.components;

import faang.school.paymentservice.dto.CurrencyResponse;
import faang.school.paymentservice.properties.LatestRatesEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({LatestRatesEndpoint.class})
public class CurrencyApiClientImpl implements CurrencyApiClient {
    private final WebClient webClient;
    private final LatestRatesEndpoint endpoint;

    @Override
    public Mono<CurrencyResponse> getCurrencyRates() {
        String url = UriComponentsBuilder.fromHttpUrl(endpoint.url())
                .queryParam("access_key", endpoint.access_key())
                .queryParam("base", endpoint.base())
                .queryParam("symbols", endpoint.symbols())
                .toUriString();

        return webClient
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(CurrencyResponse.class);
    }
}
