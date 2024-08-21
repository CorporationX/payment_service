package faang.school.paymentservice.dto.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRatesResponse {
    @NotBlank
    private String base;
    private long timestamp;
    @NotBlank
    private String date;
    @NotNull
    private Map<String, Double> rates;
}
