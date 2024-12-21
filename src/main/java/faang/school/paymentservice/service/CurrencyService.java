package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRates;
import faang.school.paymentservice.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final WebClient.Builder webClientBuilder;
    private final ExchangeRatesClient exchangeRatesClient;

    @Value("${thirdparty.exchangerates.requesturl}/")
    private String requestUrl;

    @Value("${thirdparty.exchangerates.apikey}")
    private String apiKey;

    @Value("${api.openexchangerates.app-id}")
    private String appId;

    @Value("${currency.commission}")
    private BigDecimal commission;

    @Value("${currency.baseCurrencyPayment}")
    private Currency baseCurrencyPayment;

    @CachePut(value = "exchangerates", key = "'exchangerates'")
    @Retryable(maxAttempts = 5, backoff = @Backoff(multiplier = 2))
    public ExchangeRates getCurrentExchangeRates() {
        log.info("Trying to fetch current exchange rates");
        return webClientBuilder.build()
                .get()
                .uri(requestUrl + apiKey)
                .retrieve()
                .bodyToMono(ExchangeRates.class)
                .block();
    }

    @Retryable(maxAttempts = 5, backoff = @Backoff(multiplier = 2))
    public BigDecimal convertCurrency(PaymentRequest paymentRequest) {
        if (paymentRequest.currency().equals(baseCurrencyPayment)) {
            return paymentRequest.amount();
        }
        log.info("start convertCurrency with amount: {}, from: {}, to: {}",
                paymentRequest.amount(), paymentRequest.currency().name(), baseCurrencyPayment);
        ExchangeRates currency = exchangeRatesClient.getExchangeRates(appId);

        BigDecimal exchangeRate  = BigDecimal.valueOf(currency.rates().get(paymentRequest.currency().name()));
        BigDecimal paymentInTheBaseCurrency = paymentRequest.amount()
                .divide(exchangeRate, 2, RoundingMode.HALF_UP);
        BigDecimal amountOfCommission = paymentInTheBaseCurrency.multiply(commission);

        BigDecimal result = paymentInTheBaseCurrency.subtract(amountOfCommission);
        return result.setScale(2, RoundingMode.HALF_UP);
    }
}
