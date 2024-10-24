package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRatesClient;
import faang.school.paymentservice.dto.client.ExchangeRatesResponse;
import faang.school.paymentservice.exception.CurrencyNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CurrencyConverterService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyConverterService.class);
    private final ExchangeRatesClient exchangeRatesClient;
    private final String appId;
    private final double COMMISSION_PERCENTAGE = 1.01;

    public CurrencyConverterService(ExchangeRatesClient exchangeRatesClient, @Value("${openexchangerates.appId}") String appId) {
        this.exchangeRatesClient = exchangeRatesClient;
        this.appId = appId;
    }

    public double convert(String fromCurrency, String toCurrency, double amount) {
        log.info("Начинается процесс конвертации {} {} в {}", amount, fromCurrency, toCurrency);

        ExchangeRatesResponse response = exchangeRatesClient.getLatestRate(appId);
        Map<String, Double> rates = response.getRates();

        if(!rates.containsKey(fromCurrency)) {
            log.error("Курс для валюты {} не найден", fromCurrency);
            throw new CurrencyNotFoundException("Валюта " + fromCurrency + " не найдена.");
        }

        if (!rates.containsKey(toCurrency)) {
            log.error("Курс для валюты {} не найден", toCurrency);
            throw new CurrencyNotFoundException("Валюта " + toCurrency + " не найдена.");
        }

        double fromRate = rates.get(fromCurrency);
        double toRate = rates.get(toCurrency);

        log.info("Курс для {}: {}, курс для {}: {}", fromCurrency, fromRate, toCurrency, toRate);

        double usdAmount = amount / fromRate;
        double convertedAmount  = usdAmount * toRate;
        double amountWithFee = convertedAmount * COMMISSION_PERCENTAGE;

        log.info("Конвертированная сумма с комиссией: {}", amountWithFee);
        return amountWithFee;
    }
}