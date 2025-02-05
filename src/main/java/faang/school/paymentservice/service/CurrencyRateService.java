package faang.school.paymentservice.service;

import faang.school.paymentservice.config.CurrencyRateConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRate;
import faang.school.paymentservice.exception.CurrencyRateException;
import faang.school.paymentservice.repository.CurrencyRateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyRateService {
    private final CurrencyRateRepository currencyRateRepository;
    private final CurrencyRateConfig rateConfig;

    public void save(CurrencyRate currencyRate) {
        currencyRateRepository.saveCurrencyRate(currencyRate);
    }

    public double exchange(Currency from, Currency to, BigDecimal amount) {
        Map<Currency, Double> currencyRate = currencyRateRepository.getCurrencyRates(from, to);
        double fromRate = currencyRate.get(from);
        double toRate = currencyRate.get(to);
        double conversionRateFactor = rateConfig.getConversionRateFactor();

        try {
            BigDecimal result = amount.multiply(BigDecimal.valueOf(conversionRateFactor * (toRate / fromRate)));
            return result.setScale(2, RoundingMode.HALF_UP).doubleValue();

        } catch (RuntimeException e) {
            log.error("Error while exchanging", e);
            throw new CurrencyRateException("Fail to exchange, try again later");
        }
    }

    public LocalDateTime getCurrencyRateCreatedTime(){
        return currencyRateRepository.getCreatedTime();
    }
}
