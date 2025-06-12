package faang.school.paymentservice.service.currency.conversion;

import faang.school.paymentservice.client.CurrencyConverter.CurrencyConverterClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateDto;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.exception.CurrencyConversionException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final CurrencyConverterClient currencyConverterClient;
    private Map<String, BigDecimal> rates;
    private final int delay = 3000;
    private final double commisionPercentage = 0.01;

    @PostConstruct
    public void initRates() {
        this.rates = refreshRates();
    }

    @Scheduled(cron = "3 0 * * * *")
    public void scheduledRefreshRates() {
        Map<String, BigDecimal> refreshedRates = refreshRates();
        if (!refreshedRates.isEmpty()) {
            this.rates = refreshedRates;
        }
    }

    @Retryable(
            retryFor = {CurrencyConversionException.class},
            backoff = @Backoff(delay = delay)
    )
    public Map<String, BigDecimal> refreshRates() {
        ExchangeRateDto exchangeRateDto = currencyConverterClient.getExchangeRates();
        if (exchangeRateDto == null) {
            log.warn("Exchange rate fetch failed, retrying in {} seconds...", delay);
            throw new CurrencyConversionException("Failed to fetch exchange rates");
        }
        return exchangeRateDto.getRates();
    }

    public BigDecimal getConvertedSum(PaymentRequest dto) {

        boolean notAcceptedCurrency = Arrays.stream(Currency.values())
                .noneMatch(accepted -> accepted == dto.currency());
        if (notAcceptedCurrency) {
            log.error("Non-acceptable currency transaction attempt: currency type: {} ", dto.currency());
            throw new CurrencyConversionException(String.format("Currency %s not accepted", dto.currency()));
        }
        if (dto.currency() == Currency.USD) {
            return dto.amount();
        } else {
            String codeOfUsed = dto.currency().name();
            return dto.amount()
                    .multiply(rates.get(codeOfUsed))
                    .multiply(BigDecimal.valueOf(1 - commisionPercentage));
        }
    }
}
