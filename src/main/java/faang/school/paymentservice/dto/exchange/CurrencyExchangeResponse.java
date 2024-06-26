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
    /**
     * Время получения ответа
     */
    private Long timestamp;
    /**
     * Базовая валюта, по отношению к которой будут все остальные соотношения. По умолчанию USD
     */
    private String base;
    /**
     * Мапа валюта-соотношения к base валюте
     */
    private Map<String, Double> rates;
    
    public BigDecimal getRate(Currency currency) {
        return BigDecimal.valueOf(rates.get(currency.name()));
    }
}
