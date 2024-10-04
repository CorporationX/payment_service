package faang.school.paymentservice.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class Rate {
    private String base;
    private Map<String, Double> rates;
}
