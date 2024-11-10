package faang.school.paymentservice.service.exchangerate;

import faang.school.paymentservice.client.CurrencyRatesClient;
import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.ExchangeRateResponseDto;
import faang.school.paymentservice.exception.ResponseDtoNotFoundException;
import faang.school.paymentservice.model.Currency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    @Value("${currency.openexchangerates.appId}")
    private String appId;

    @Value("${currency.commission}")
    private BigDecimal commission;

    private final ExchangeRatesClient exchangeRatesClient;
    private final CurrencyRatesClient currencyRatesClient;
    private final Map<String, Double> currencyRates = new ConcurrentHashMap<>();

    public void updateCurrencyRates() {
        currencyRatesClient.fetchRates().subscribe(currencyRates::putAll);
    }

    public Double getRate(String currency) {
        return currencyRates.get(currency);
    }

    public BigDecimal convertCurrency(BigDecimal amount, Currency fromCurrency, Currency toCurrency) {
        log.info("start convertCurrency with amount: {}, from: {}, to: {}", amount, fromCurrency, toCurrency);

        ExchangeRateResponseDto responseDto = exchangeRatesClient
                .getCurrentExchangeRates(appId, fromCurrency, toCurrency)
                .orElseThrow(() -> new ResponseDtoNotFoundException("Response from openexchangerates.org not received"));

        validationExchangeRateAmount(responseDto, toCurrency);
        BigDecimal exchangeRate = BigDecimal.valueOf(responseDto.getRates().get(toCurrency.name()));
        log.info("exchangeRate: {}", exchangeRate);

        BigDecimal resultExchangeRate = amount.multiply(exchangeRate).multiply(BigDecimal.ONE.add(commission));
        log.info("finish convertCurrency with: {}", resultExchangeRate);

        return resultExchangeRate;
    }

    private void validationExchangeRateAmount(ExchangeRateResponseDto responseDto, Currency toCurrency) {
        if (responseDto.getRates().get(toCurrency.name()) == null) {
            log.error("ExchangeRate is null!");
            throw new IllegalArgumentException("Exchange rate not found for currency: " + toCurrency.name());
        }
    }
}
