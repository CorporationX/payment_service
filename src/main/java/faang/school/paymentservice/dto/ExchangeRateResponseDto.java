package faang.school.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@Builder
@ToString
public class ExchangeRateResponseDto {

    @NotNull
    private String disclaimer;

    @NotNull
    private String license;

    @NotNull
    private long timestamp;

    @NotNull
    private String base;

    @NotNull
    private Map<String, Double> rates;
}
