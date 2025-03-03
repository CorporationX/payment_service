package faang.school.paymentservice.service;

import faang.school.paymentservice.client.CurrencyConverterClient;
import faang.school.paymentservice.dto.payment.Currency;
import faang.school.paymentservice.dto.payment.ExchangeRateResponse;
import faang.school.paymentservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyConverterService {

    private final CurrencyConverterClient converterClient;

    @Value("${open-exchange-rates.key}")
    private String appId;

    @Value("${open-exchange-rates.service-fee}")
    private double serviceFee;


    public ExchangeRateResponse getRateResponse(Currency baseCurrency) {
        return converterClient.getExchangeRate(appId, baseCurrency);
    }

    public BigDecimal convertCurrency(BigDecimal amount, Currency baseCurrency, Currency toCurrency) {
        Map<String, BigDecimal> rates = getRateResponse(baseCurrency).getRates();

        if (rates != null && rates.containsKey(toCurrency.toString())) {
            return amount
                    .multiply(rates.get(toCurrency.toString()))
                    .multiply(new BigDecimal(1.0 + serviceFee))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            throw new DataValidationException("Не найден код валюты %s для конвертации"
                    .formatted(toCurrency));
        }
    }
}
