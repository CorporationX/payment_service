package faang.school.paymentservice.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import faang.school.paymentservice.model.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Класс ответа от API обменного курса
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyExchangeResponse {
    private Long timestamp;
    private String base;
    private Map<String, Double> rates;
    public BigDecimal getRate(Currency currency) {
        return BigDecimal.valueOf(rates.get(currency.name()));
    }
}

