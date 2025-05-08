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
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractExchangeRateProvider implements ExchangeRateProvider {

    protected static final Set<String> ALLOWED_CURRENCIES =
            Arrays.stream(Currency.values())
                    .map(Enum::name)
                    .collect(Collectors.toSet());

    protected final WebClient webClient;
    protected final String accessKey;
    protected final String endpoint;


    public AbstractExchangeRateProvider(String baseUrl,
                                        String accessKey,
                                        String endpoint) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
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
            throw new ExchangeRatesException("Failed to fetch exchange rates: response is null");
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

    private Map<Currency, BigDecimal> filterRates(Map<String, BigDecimal> rates) {
        Map<Currency, BigDecimal> filteredRates = rates.entrySet().stream()
                .filter(e -> isAllowedCurrency(e.getKey()))
                .filter(e -> isValidRate(e.getValue()))
                .collect(Collectors.toMap(
                        e -> Currency.valueOf(e.getKey()),
                        Map.Entry::getValue
                ));

        if (filteredRates.size() < ALLOWED_CURRENCIES.size()) {
            log.error("Failed to fetch exchange rates: some currencies are not allowed");
            throw new ExchangeRatesException("Failed to fetch exchange rates: some currencies are not allowed");
        }

        return filteredRates;
    }

    private boolean isAllowedCurrency(String currency) {
        return ALLOWED_CURRENCIES.contains(currency);
    }

    private boolean isValidRate(BigDecimal rate) {
        return rate != null && rate.compareTo(BigDecimal.ZERO) > 0;
    }
}
