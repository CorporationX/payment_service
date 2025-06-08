package faang.school.paymentservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExchangeRateDto {
    private String disclaimer;
    private String license;
    private long timestamp;
    private String base;
    private Map<String, BigDecimal> rates;
}
