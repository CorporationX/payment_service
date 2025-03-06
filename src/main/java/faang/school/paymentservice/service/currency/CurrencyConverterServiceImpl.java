package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.client.ExchangeServiceClient;
import faang.school.paymentservice.dto.ExchangeResponse;
import faang.school.paymentservice.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.properties.ExchangeServiceProperties;
import com.fasterxml.jackson.core.type.TypeReference;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CurrencyConverterServiceImpl implements CurrencyConverterService {

    private final ExchangeServiceClient exchangeServiceClient;
    private final ExchangeServiceProperties exchangeServiceProperties;
    private final RedisService redisService;
    @Value("${services.exchange-service.redis-key}")
    private String redisKey;

    @Override
    public BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency, BigDecimal amount) {

        log.info("Start, convert currency from {} to {}", fromCurrency, toCurrency);
        Map<String, Double> rates = getExchangeRates();

        BigDecimal fromRate = getRate(rates, fromCurrency);
        BigDecimal toRate = getRate(rates, toCurrency);

        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Exchange rate not found for specified currencies: " + fromCurrency.name() + " to " + toCurrency.name());
        }

        BigDecimal rate = toRate.divide(fromRate, RoundingMode.HALF_UP);

        return calculateAmountWithCommission(amount, rate);
    }

    private Map<String, Double> getExchangeRates() {
        Map<String, Double> rates = redisService.get(redisKey, new TypeReference<>() {
        });
        if (rates == null) {
            log.info("Exchange rates not found in cache, fetching from external API");
            ExchangeResponse response = exchangeServiceClient.exchange(exchangeServiceProperties.getToken());
            rates = response.rates();
            redisService.save(redisKey, rates);
        }
        return rates;
    }

    private BigDecimal calculateAmountWithCommission(BigDecimal amount, BigDecimal rate) {

        BigDecimal convertedAmount = amount.multiply(rate);
        BigDecimal commission = convertedAmount.multiply(new BigDecimal(exchangeServiceProperties.getCommissionRate()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return convertedAmount.add(commission);
    }

    private BigDecimal getRate(Map<String, Double> rates, Currency currency) {

        Double rate = rates.get(currency.name());
        if (rate == null) {
            throw new IllegalArgumentException("Exchange rate not found for currency: " + currency.name());
        }
        return BigDecimal.valueOf(rate);
    }
}