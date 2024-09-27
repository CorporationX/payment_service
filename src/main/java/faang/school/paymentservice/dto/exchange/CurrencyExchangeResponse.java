package faang.school.paymentservice.dto.exchange;

import java.math.BigDecimal;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import faang.school.paymentservice.dto.Currency;
import lombok.*;

/**
 * Класс ответа от API обменного курса
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrencyExchangeResponse {
    @JsonProperty("base_code")
    private String baseCode; //Базовая валюта. По умолчанию USD.
    @JsonProperty("conversion_rates")
    private Map<String, Double> conversionRates; //Cоотношения к base валюте

    public BigDecimal getRate(Currency currency) {
        return BigDecimal.valueOf(conversionRates.get(currency.name()));
    }

    public BigDecimal setConversionRates(Currency currency, BigDecimal number) {
        return BigDecimal.valueOf(conversionRates.put(currency.name(), number.doubleValue()));
    }
}