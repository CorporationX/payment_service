package faang.school.paymentservice.service.currencyconverter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.client.CurrencyConverterClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyConverterDto;
import faang.school.paymentservice.dto.CurrencyRate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;


@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyConverterService {
    private final ObjectMapper objectMapper;
    private final CurrencyConverterClient currencyConverterClient;
    @Value("${currency-converter.percentCommission}")
    private BigDecimal percentCommission;
    @Value("${currency-converter.foreignConverterUrl}")
    private String converterUrl;
    @Value("${currency-converter.foreignConverterAppId}")
    private String converterAppId;
    public CurrencyConverterDto convert(CurrencyConverterDto dto) {
        // Будет работать только если одна из валют - USD
        // для других случаев необходимо выбросить исключение и сделать логгирование
        String transmittedCurrency;
        String receivedCurrency;
        if (dto.getTransmittedCurrency() == Currency.USD) {
            transmittedCurrency = dto.getTransmittedCurrency().toString();
            receivedCurrency = dto.getReceivedCurrency().toString();
        } else {
            transmittedCurrency = dto.getReceivedCurrency().toString();
            receivedCurrency = dto.getTransmittedCurrency().toString();
        }
        ResponseEntity<String> responseEntity = currencyConverterClient.getCurrencyRate(
                converterAppId,
                transmittedCurrency,
                receivedCurrency
        );
        String responseJson = responseEntity.getBody();
        try {
            CurrencyRate currencyRate = objectMapper.readValue(responseJson, CurrencyRate.class);
            BigDecimal rateValue = BigDecimal.valueOf(currencyRate.getRates().get(receivedCurrency));
            BigDecimal commission;
            BigDecimal convertSum;
            BigDecimal transmittedSum = dto.getTransmittedSum();
            commission = transmittedSum.multiply(percentCommission);
            if (dto.getTransmittedCurrency() == Currency.USD) {
                convertSum = rateValue.multiply(dto.getTransmittedSum());
            } else {
                convertSum = transmittedSum.divide(rateValue, RoundingMode.HALF_UP);
            }
            dto.setReceivedSum(convertSum);
            dto.setCommission(commission);
            dto.setTotalSum(convertSum.add(commission));
            return dto;
        } catch (JsonProcessingException | RuntimeException e) {
            log.error(String.valueOf(e));
            throw new RuntimeException(e);
        }
    }
}
