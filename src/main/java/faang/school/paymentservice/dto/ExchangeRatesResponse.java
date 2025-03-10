package faang.school.paymentservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExchangeRatesResponse {
    private String disclaimer;
    private String license;
    private long timestamp;
    private String base;
    private Map<Currency, BigDecimal> rates;
}
