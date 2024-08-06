package faang.school.paymentservice.dto.exchange;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import faang.school.paymentservice.dto.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Класс ответа от API обменного курса
 */
@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyExchangeResponse {
    private String base_code; //Базовая валюта. По умолчанию USD.
    private Map<String, Double> conversion_rates; //Cоотношения к base валюте

    public BigDecimal getRate(Currency currency) {
        return BigDecimal.valueOf(conversion_rates.get(currency.name()));
    }
}