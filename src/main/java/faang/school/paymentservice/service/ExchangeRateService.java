package faang.school.paymentservice.service;

import faang.school.paymentservice.client.OpenExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRatesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {
    private final OpenExchangeRatesClient openExchangeRatesClient;

    public ExchangeRatesResponse getLatestExchangeRates(Currency current, Currency target) {
        return openExchangeRatesClient.getExchangeRates(current, target);
    }
}
