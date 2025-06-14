package faang.school.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class LatestRatesResponseDto {
    private String disclaimer;
    private String license;
    private Long timestamp;
    private String base;
    private Map<String, BigDecimal> rates;
}
