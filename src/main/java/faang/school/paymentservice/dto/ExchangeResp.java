package faang.school.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ExchangeResp {
    private String disclaimer;
    private String license;
    private String base;
    private Long timestamp;
    private Map<String, Double> rates;
}
