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
    private final int COMMISION = 1;

    @Value("${services.openexchange.appId}")
    private String appId;

    public BigDecimal getLatestExchangeRates(PaymentRequest paymentRequest, Currency currency) {
        String jsonFromExtService = openExchangeRatesClient.getLatest(appId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            CurrencyDto currencyDto = objectMapper.readValue(jsonFromExtService, CurrencyDto.class);
//            BigDecimal result = convertCurrency(paymentRequest.amount(), currencyDto.getRates().get(currency.toString()));
            if (currencyDto.getRates().get(currency) == null) {
                throw new NotFoundException("Currency not found");
            }
            BigDecimal result = convertCurrency(paymentRequest.amount(), currencyDto.getRates().get(currency.toString()));
            result = addCommission(result, COMMISION);
            return result;
//            System.out.println(currencyDto.getBase());
//            System.out.println("RUB" + currencyDto.getRates().get("RUB"));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private BigDecimal addCommission(BigDecimal amount, int commissionPercent) {
        BigDecimal temp = amount.divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(commissionPercent));
        amount = amount.add(temp);
        return amount;
    }

    private BigDecimal convertCurrency(BigDecimal amount, double rate) {
        return amount.multiply(BigDecimal.valueOf(rate));
    }
}
