package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.service.currency.rates.ExchangeRatesService;
import faang.school.paymentservice.validator.CurrencyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyService {

    @Value("${currency-api.commission}")
    private BigDecimal commission;

    @Value("${currency-api.baseCurrency}")
    private Currency baseCurrency;

    private final RedisTemplate<String, Double> redisTemplate;
    private final ExchangeRatesService exchangeRatesService;
    private final CurrencyValidator currencyValidator;

    public String getExchangeRates() {
        return exchangeRatesService.getExchangeRates();
    }

    public BigDecimal convertCurrency(PaymentRequestDto dto, String formattedSum) {
        if (!dto.getCurrency().name().equals(baseCurrency.name())) {
            Double exchangeRateByCurrency = getExchangeRateByCurrency(dto.getCurrency());
            BigDecimal amountAfterConvert = BigDecimal.valueOf(Double.parseDouble(formattedSum))
                    .divide(BigDecimal.valueOf(exchangeRateByCurrency), 2, RoundingMode.DOWN);
            BigDecimal commissionAmount = amountAfterConvert.multiply(commission);
            BigDecimal amountAfterCommission = amountAfterConvert.subtract(commissionAmount);
            currencyValidator.validateMinimumTransferAmount(amountAfterCommission);

            log.info("The currency from the payment request was successfully converted taking into account the commission.");
            return amountAfterCommission;
        }
        currencyValidator.validateMinimumTransferAmount(dto.getAmount());
        return dto.getAmount();
    }

    public Double getExchangeRateByCurrency(Currency currency) {
        Set<String> keys = redisTemplate.keys(currency.name());
        Double rate = 0.0;
        if (keys != null) {
            for (String key : keys) {
                rate = redisTemplate.opsForValue().get(key);
                if (rate != null) {
                    log.debug("The cached exchange rate {} was obtained from Redis.", currency.name());
                    return rate;
                }
            }
        }
        log.warn("{} rate not found in Redis storage.", currency.name());
        return rate;
    }
}
