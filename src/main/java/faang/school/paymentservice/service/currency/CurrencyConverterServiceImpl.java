package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.client.ExchangeServiceClient;
import faang.school.paymentservice.dto.ExchangeResponse;
import faang.school.paymentservice.dto.PaymentRequest;
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
import java.util.Optional;

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
    public BigDecimal convertCurrency(PaymentRequest dto) {
        log.info("Start, convert currency from {} to {}", dto.fromCurrency(), dto.toCurrency());
        Map<String, Double> rates = getExchangeRates();
        BigDecimal fromRate = getRate(rates, dto.fromCurrency());
        BigDecimal toRate = getRate(rates, dto.toCurrency());
        BigDecimal rate = toRate.divide(fromRate, RoundingMode.HALF_UP);
        return calculateAmountWithCommission(dto.amount(), rate);
    }

    private Map<String, Double> getExchangeRates() {
        Optional<Map<String, Double>> optionalRates = redisService.get(redisKey, new TypeReference<>() {
        });
        Map<String, Double> rates = optionalRates.orElseGet(() -> {
            log.info("Exchange rates not found in cache, fetching from external API");
            ExchangeResponse response = exchangeServiceClient.exchange(exchangeServiceProperties.getToken());
            Map<String, Double> fetchedRates = response.rates();
            redisService.save(redisKey, fetchedRates);
            return fetchedRates;
        });
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