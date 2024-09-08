package faang.school.paymentservice.client.config;

import faang.school.paymentservice.dto.Currency;
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

    public BigDecimal getExchangeRate(Currency baseCurrency, Currency fromCurrency) {
        ExchangeRatesResponse exchangeRatesResponse = exchangeRatesClient.getExchangeRates(appId, baseCurrency, List.of(fromCurrency));

        if (exchangeRatesResponse.getRates().isEmpty()) {
            String errorMessage = "Error when trying to get exchange rates";
            log.error("{} - {}: {}",
                    getClass().getSimpleName(),
                    new Object() {
                    }.getClass().getEnclosingMethod().getName(),
                    errorMessage);
            throw new ExchangeRatesException(errorMessage);
        }

        return BigDecimal.valueOf(exchangeRatesResponse.getRates().get(fromCurrency.toString()));
    }
}
