package faang.school.paymentservice.service.currency.implementations;

import faang.school.paymentservice.dto.PrimaryExchangeRateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;
import java.util.function.Function;

@Component
@Slf4j
public class PrimaryExchangeRateProvider extends AbstractExchangeRateProvider<PrimaryExchangeRateResponse> {

    public PrimaryExchangeRateProvider(@Value("${currency.api.primary-provider.base-url}") String baseUrl,
                                       @Value("${currency.api.primary-provider.accessKey}") String accessKey,
                                       @Value("${currency.api.primary-provider.endpoint}") String endpoint) {
        super(baseUrl, accessKey, endpoint);
    }

    @Override
    public PrimaryExchangeRateResponse fetchResponse() {
        return webClient.get().uri(buildUri())
                .retrieve()
                .bodyToMono(PrimaryExchangeRateResponse.class)
                .doOnError(error -> log.error("Error fetching exchange rates: ", error))
                .block();
    }

    @Override
    protected Function<UriBuilder, URI> buildUri() {
        return uriBuilder -> uriBuilder
                .path("/" + accessKey + endpoint)
                .build();
    }

    @Override
    protected Integer getTimestamp(PrimaryExchangeRateResponse response) {
        return response.getTimestamp();
    }

    @Override
    protected String getBase(PrimaryExchangeRateResponse response) {
        return response.getBase();
    }

    @Override
    protected Map<String, BigDecimal> getRates(PrimaryExchangeRateResponse response) {
        return response.getRates();
    }
}
