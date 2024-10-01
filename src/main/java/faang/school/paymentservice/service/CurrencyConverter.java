package faang.school.paymentservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.client.OpenExchangeRatesClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyDto;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyConverter {
    private final OpenExchangeRatesClient openExchangeRatesClient;
    private final int COMMISSION_PERCENT = 1;

    @Value("${services.openexchange.appId}")
    private String appId;

    public BigDecimal getLatestExchangeRates(PaymentRequest paymentRequest, Currency currency) {
        String jsonFromExtService = openExchangeRatesClient.getLatest(appId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CurrencyDto mapCurrencies = objectMapper.readValue(jsonFromExtService, CurrencyDto.class);
            if (mapCurrencies.getRates().get(currency.toString()) == null) {
                throw new NotFoundException("Currency not found");
            }

            BigDecimal result;
            if (!paymentRequest.currency().equals(currency)) {
                result = convertCurrency(paymentRequest.amount(), mapCurrencies.getRates().get(currency.toString()));
            } else {
                result = paymentRequest.amount();
            }
            result = addCommission(result, COMMISSION_PERCENT);
            return result;
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private BigDecimal addCommission(BigDecimal amount, int commissionPercent) {
        BigDecimal percentAmount = amount.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(commissionPercent));
        amount = amount.add(percentAmount);
        return amount;
    }

    private BigDecimal convertCurrency(BigDecimal amount, double rate) {
        return amount.multiply(BigDecimal.valueOf(rate));
    }
}
