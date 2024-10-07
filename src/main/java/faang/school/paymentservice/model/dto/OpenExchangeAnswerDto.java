package faang.school.paymentservice.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class OpenExchangeAnswerDto {
    private String disclaimer;
    private String license;
    private Long timestamp;
    private String base;
    private double currentRate;
    private Map<String, Double> rates;
}
