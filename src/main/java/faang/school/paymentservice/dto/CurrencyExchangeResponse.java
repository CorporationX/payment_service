package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.Map;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Ответ с информацией о валютном курсе")
public record CurrencyExchangeResponse(
        @Schema(description = "Временная метка в миллисекундах", example = "1682563200000")
        Long timeStamp,
        @Schema(description = "Базовая валюта", example = "USD")
        String base,
        @Schema(description = "Курсы валют относительно базовой валюты")
        Map<String, Double> rates
) {
    public BigDecimal getRate(Currency currency) {
        return BigDecimal.valueOf(rates.get(currency.name()));
    }
}
