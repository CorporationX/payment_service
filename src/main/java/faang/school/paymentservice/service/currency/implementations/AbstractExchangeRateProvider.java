package faang.school.paymentservice.service.currency.implementations;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateResponse;
import faang.school.paymentservice.dto.StringExchangeRateResponse;
import faang.school.paymentservice.exception.ExchangeRatesException;
import faang.school.paymentservice.service.currency.interfaces.ExchangeRateProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractExchangeRateProvider implements ExchangeRateProvider {
    protected final WebClient webClient;
    protected final String baseUrl;
    protected final String accessKey;
    protected final String endpoint;

    public AbstractExchangeRateProvider(String baseUrl,
                                        String accessKey,
                                        String endpoint) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.baseUrl = baseUrl;
        this.accessKey = accessKey;
        this.endpoint = endpoint;
    }

    protected abstract StringExchangeRateResponse fetchResponse();
    protected abstract Function<UriBuilder, URI> buildUri();

    public ExchangeRateResponse getExchangeRates() {
        ExchangeRateResponse exchangeRateResponse = new ExchangeRateResponse();
        StringExchangeRateResponse response = fetchResponse();
        if (response == null) {
            log.error("Failed to fetch exchange rates: response is null");
            throw new RuntimeException("Failed to fetch exchange rates: response is null");
        }
        exchangeRateResponse.setTimestamp(getTimestamp(response));
        exchangeRateResponse.setBase(getBase(response));
        exchangeRateResponse.setRates(filterRates(getRates(response)));
        log.info("Fetched exchange rates successfully: {}", exchangeRateResponse);
        return exchangeRateResponse;
    }

    protected Integer getTimestamp(StringExchangeRateResponse response) {
        return response.getTimestamp();
    }

    protected String getBase(StringExchangeRateResponse response) {
        return response.getBase();
    }

    protected Map<String, BigDecimal> getRates(StringExchangeRateResponse response) {
        if (response.getRates() == null || response.getRates().isEmpty()) {
            log.error("Failed to fetch exchange rates: rates are null");
            throw new ExchangeRatesException("Failed to fetch exchange rates: rates are null");
        }
        return response.getRates();
    }

    protected Map<Currency, BigDecimal> filterRates(Map<String, BigDecimal> rates) {
        return rates.entrySet().stream()
                .filter(e -> isAllowedCurrency(e.getKey()))
                .collect(Collectors.toMap(
                        e -> Currency.valueOf(e.getKey()),
                        Map.Entry::getValue
                ));
    }

    protected boolean isAllowedCurrency(String currency) {
        try {
            Currency.valueOf(currency);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
