package faang.school.paymentservice.client.config;

import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import faang.school.paymentservice.exception.ExchangeRatesException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class ExchangeRatesInterceptor {
    private final ExchangeRatesClient exchangeRatesClient;

    @Value("${feign_client.exchange_rates.appId}")
    private String appId;

    public BigDecimal getExchangeRate(String baseCurrency, String fromCurrency) {
        ExchangeRatesResponse exchangeRatesResponse = exchangeRatesClient.getExchangeRates(appId, baseCurrency, List.of(fromCurrency));

        if (exchangeRatesResponse.getRates().isEmpty()) {
            String errorMessage = "Error when trying to get exchange rates";
            log.error("ExchangeRatesInterceptor.class " + errorMessage);
            throw new ExchangeRatesException(errorMessage);
        }
        Double exchangeRate = exchangeRatesResponse.getRates().get(fromCurrency);

        return BigDecimal.valueOf(exchangeRate);
    }
}
