package faang.school.paymentservice.service.currency.conversion;

import faang.school.paymentservice.client.converter.CurrencyConverterClient;
import faang.school.paymentservice.config.CurrencyConverterConfigurationProperties;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateDto;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.exception.CurrencyConversionException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConversionServiceImpl implements CurrencyConversionService {

    private final CurrencyConverterClient currencyConverterClient;
    private final CurrencyConverterConfigurationProperties currencyProperties;

    private Map<String, BigDecimal> rates;

    @PostConstruct
    public void initRates() {
        this.rates = getExchangeRates();
    }

    @Override
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
                    .multiply(BigDecimal.valueOf(1 - currencyProperties.getCommissionPercentage()));
        }
    }

    @Override
    public Map<String, BigDecimal> getExchangeRates() {
        ExchangeRateDto exchangeRateDto = currencyConverterClient.getExchangeRates();
        if (exchangeRateDto == null) {
            throw new CurrencyConversionException("Failed to fetch exchange rates");
        }
        return exchangeRateDto.getRates();
    }

    @Override
    public void updateRates(Map<String, BigDecimal> newRates) {
        this.rates = newRates;
    }

}
