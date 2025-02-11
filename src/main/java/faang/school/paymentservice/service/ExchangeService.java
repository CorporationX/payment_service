package faang.school.paymentservice.service;

import faang.school.paymentservice.client.PaymentServiceClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeResp;
import faang.school.paymentservice.exception.CurrencyNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExchangeService {

    private final PaymentServiceClient paymentServiceClient;

    @Value("${services.exchange-service.app-id}")
    private String appId;

    public Double convert(Double value, Currency currency) {
        ExchangeResp exchange = paymentServiceClient.getExchange(appId);
        if(!exchange.getRates().containsKey(currency.name())) {
            throw new CurrencyNotFoundException("Currency not found: " + currency);
        }

        Double exchangeRate = exchange.getRates().get(currency.name());
        double convertedAmount = value * exchangeRate;
        double commission = convertedAmount * 0.01;

        return convertedAmount + commission;
    }
}
