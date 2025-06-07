package faang.school.paymentservice.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import faang.school.paymentservice.client.OpenexchangeratesServiceClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {
    private final OpenexchangeratesServiceClient openexchangeratesServiceClient;

    @Value("${open-exchange-rates.appId}")
    private String appId;

    @Value("${open-exchange-rates.conversion-commission}")
    private String commissionSettings;
    
    @Override
    public BigDecimal exchange(BigDecimal amount, Currency paymentCurrency) {
        BigDecimal commissionBigDecimal = BigDecimal.valueOf(Double.valueOf(commissionSettings));
        ExchangeRateResponse response = openexchangeratesServiceClient.getExchangeRates(
            appId, 
            paymentCurrency.name(), 
            true
        );
        log.info("Exchange rate response: {}", response);

        BigDecimal convertedAmount = amount;
        if (response.getRate(paymentCurrency.name()) == null) {
            log.error("Exchange rate not found");
            throw new RuntimeException("Exchange rate not found");
        }

        Double exchangeRate = response.getRate(paymentCurrency.name());
        convertedAmount = amount.multiply(BigDecimal.valueOf(exchangeRate)).multiply(commissionBigDecimal); 

        return convertedAmount;
    }
}
