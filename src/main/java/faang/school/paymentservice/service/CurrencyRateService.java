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

@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyRateService {
    private final CurrencyRateRepository currencyRateRepository;
    private final CurrencyRateConfig rateConfig;

    public void save(CurrencyRate currencyRate) {
        currencyRateRepository.saveCurrencyRate(currencyRate);
    }

    public CurrencyRate get() {
        return currencyRateRepository.getCurrencyRate();
    }

    public double exchange(Currency from, Currency to, BigDecimal amount) {
        CurrencyRate currencyRate = currencyRateRepository.getCurrencyRate();
        double fromRate = currencyRate.getRates().get(from);
        double toRate = currencyRate.getRates().get(to);
        double conversionRateFactor = rateConfig.getConversionRateFactor();

        try {
            BigDecimal result = amount.multiply(BigDecimal.valueOf(conversionRateFactor * (toRate / fromRate)));
            return result.setScale(2, RoundingMode.HALF_UP).doubleValue();

        } catch (RuntimeException e) {
            log.error("Error while exchanging", e);
            throw new CurrencyRateException("Fail to exchange, try again later");
        }
    }
}
