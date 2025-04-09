package faang.school.paymentservice.service.currency.implementations;

import faang.school.paymentservice.dto.SlaveExchangeRateResponse;
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
public class SlaveExchangeRateProvider extends AbstractExchangeRateProvider<SlaveExchangeRateResponse> {

    public SlaveExchangeRateProvider(@Value("${currency.api.slave-provider.base-url}") String baseUrl,
                                     @Value("${currency.api.slave-provider.accessKey}") String accessKey,
                                     @Value("${currency.api.slave-provider.endpoint}") String endpoint) {
        super(baseUrl, accessKey, endpoint);
    }

    @Override
    public SlaveExchangeRateResponse fetchResponse() {
        return webClient.get().uri(buildUri())
                .retrieve()
                .bodyToMono(SlaveExchangeRateResponse.class)
                .doOnError(error -> log.error("Error fetching exchange rates: ", error))
                .block();
    }

    @Override
    protected Function<UriBuilder, URI> buildUri() {
        return uriBuilder -> uriBuilder
                .path(endpoint)
                .queryParam("access_key", accessKey)
                .build();
    }

    @Override
    protected Integer getTimestamp(SlaveExchangeRateResponse response) {
        return response.getTimestamp();
    }

    @Override
    protected String getBase(SlaveExchangeRateResponse response) {
        return response.getBase();
    }

    @Override
    protected Map<String, BigDecimal> getRates(SlaveExchangeRateResponse response) {
        return response.getRates();
    }
}
