package faang.school.paymentservice.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

import faang.school.paymentservice.client.OpenexchangeratesServiceClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "openexchangerates")
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {
    private final static BigDecimal COMISSION = BigDecimal.valueOf(1.01);
    private final OpenexchangeratesServiceClient openexchangeratesServiceClient;
    
    @Value("${openexchangerates.appId}")
    private String appId;

    @Override
    public BigDecimal exchange(BigDecimal amount, Currency paymentCurrency) {
        ExchangeRateResponse response = openexchangeratesServiceClient.getExchangeRates(
            appId, 
            paymentCurrency.name(), 
            true
        );
        log.info("Exchange rate response: {}", response);

        BigDecimal convertedAmount = amount;
        if (response != null && response.getRate(paymentCurrency.name()) != null) {
            Double exchangeRate = response.getRate(paymentCurrency.name());
            convertedAmount = amount.multiply(BigDecimal.valueOf(exchangeRate)).multiply(COMISSION); 
        } else {
            log.error("Exchange rate not found");
            throw new RuntimeException("Exchange rate not found");
        }

        return convertedAmount;
    }
}
