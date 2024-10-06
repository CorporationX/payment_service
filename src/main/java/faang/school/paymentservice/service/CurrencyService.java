package faang.school.paymentservice.service;

import faang.school.paymentservice.config.CurrencyExchangeConfig;
import faang.school.paymentservice.client.CurrencyConverterClient;
import faang.school.paymentservice.response.CurrencyExchangeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CurrencyExchangeConfig config;
    private final CurrencyConverterClient currencyConverter;

    public CurrencyExchangeResponse getCurrencyExchange() {
        return currencyConverter.getCurrentCurrencyExchangeRate(config.getAppId());
    }
}
