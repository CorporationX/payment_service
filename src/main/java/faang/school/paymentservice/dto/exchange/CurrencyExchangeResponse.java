package faang.school.paymentservice.dto.exchange;

import lombok.Data;

import java.util.Map;

@Data
public class CurrencyExchangeResponse {
    private Long timestamp;

    private String base;

    private Map<String, Double> rates;
}