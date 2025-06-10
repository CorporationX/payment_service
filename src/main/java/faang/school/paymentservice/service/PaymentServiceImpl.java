package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.ExchangeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl {
    @Value("${oxr.app-id}")
    private String appId;
    @Value("${oxr.base-url}")
    private String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public BigDecimal converter(BigDecimal amount, String fromCurrency, String toCurrency) {
        BigDecimal result = converterCurrency(fromCurrency, toCurrency);
        return amount.multiply(result).setScale(2, RoundingMode.HALF_UP);
    }

    public ExchangeResponseDto getLatestRates() {
        String url = String.format("%s/latest.json?app_id=%s", baseUrl, appId);
        if(url == null) {
            throw new IllegalArgumentException("Url null");
        }
        return restTemplate.getForEntity(url, ExchangeResponseDto.class).getBody();
    }

    private BigDecimal converterCurrency(String fromCurrency, String toCurrency) {

        Number from = getLatestRates().getRates().entrySet()
                .stream()
                .filter(rate -> rate.getKey().equals(fromCurrency))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Currency Not Found " + fromCurrency))
                .getValue();

        Number to = getLatestRates().getRates().entrySet()
                .stream()
                .filter(rate -> rate.getKey().equals(toCurrency))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Currency Not Found " + toCurrency))
                .getValue();

        return BigDecimal.valueOf(to.doubleValue()).divide(BigDecimal.valueOf(from.doubleValue()),
                10, RoundingMode.HALF_UP);
    }

}
