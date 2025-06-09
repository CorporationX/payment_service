package faang.school.paymentservice.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ExchangeRateResponseDto {
    private String disclaimer;
    private String license;
    private String base;
    private Map<String, Double> rates;
}
