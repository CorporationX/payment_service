package faang.school.paymentservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ExchangeRateResponse {
    private String base;
    private String date;
    private Map<String, BigDecimal> rates;
}
