package faang.school.paymentservice.dto;

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
    private String disclaimer;
    private String license;
    private long timestamp;
    private String base;
    private Map<String, Double> rates;
}
