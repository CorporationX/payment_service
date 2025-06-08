package faang.school.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExchangeResponseDto {
    private String url;
    private String apiId;
    private Map<String, Double> rates;
}
