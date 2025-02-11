package faang.school.paymentservice.dto.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeRates {
    @NotBlank
    private String base;
    private long timestamp;
    @NotBlank
    private String date;
    @NotNull
    private Map<String, Double> rates;
}
