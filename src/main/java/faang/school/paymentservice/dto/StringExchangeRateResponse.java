package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class StringExchangeRateResponse {

    @JsonAlias({"timestamp", "time_last_update_unix"})
    private Integer timestamp;
    @JsonAlias({"base", "base_code"})
    private String base;
    @JsonAlias({"rates", "conversion_rates"})
    private Map<String, BigDecimal> rates;

}
