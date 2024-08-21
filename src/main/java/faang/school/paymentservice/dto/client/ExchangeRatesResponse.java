package faang.school.paymentservice.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRatesResponse {
    private String base;
    private long timestamp;
    private String date;
    private Map<String,Double> rates;
}
