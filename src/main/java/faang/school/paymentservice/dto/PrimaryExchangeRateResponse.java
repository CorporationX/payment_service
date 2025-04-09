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
public class PrimaryExchangeRateResponse {

    @JsonProperty("time_last_update_unix")
    private Integer timestamp;
    @JsonProperty("base_code")
    private String base;
    @JsonProperty("conversion_rates")
    private Map<String, BigDecimal> rates;

}
