package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class SlaveExchangeRateResponse {

    @JsonProperty("timestamp")
    private Integer timestamp;
    @JsonProperty("base")
    private String base;
    @JsonProperty("rates")
    private Map<String, BigDecimal> rates;

}
