package faang.school.paymentservice.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import faang.school.paymentservice.client.OpenexchangeConfig;
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
    private final OpenexchangeConfig openexchangeConfig;

    @Override
    public BigDecimal exchange(BigDecimal amount, Currency paymentCurrency) {
        BigDecimal commissionBigDecimal = BigDecimal.valueOf(Double.valueOf(openexchangeConfig.conversionCommission()));
        ExchangeRateResponse exchangeRateData = openexchangeratesServiceClient.getExchangeRates(
            openexchangeConfig.appId(), 
            paymentCurrency.name(), 
            true
        );
        log.info("Exchange rate response: {}", exchangeRateData);

        if (exchangeRateData.getRate(paymentCurrency.name()) == null) {
            log.error("Exchange rate not found");
            throw new RuntimeException("Exchange rate not found");
        }

        Double exchangeRate = exchangeRateData.getRate(paymentCurrency.name());
        return amount.multiply(BigDecimal.valueOf(exchangeRate)).multiply(commissionBigDecimal); 
    }
}
