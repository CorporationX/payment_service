package faang.school.paymentservice.dto.exchange;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import faang.school.paymentservice.dto.Currency;
import lombok.Data;

/**
 * Класс ответа от API обменного курса
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyExchangeResponse {
    private Long timestamp; //Время ответа
    private String base; //Базовая валюта. По умолчанию USD.
    private Map<String, Double> rates; //Cоотношения к base валюте

    public BigDecimal getRate(Currency currency) {
        return BigDecimal.valueOf(rates.get(currency.name()));
    }
}