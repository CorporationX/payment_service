package faang.school.paymentservice.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class Rate implements Serializable {
    private String base;
    private Map<String, Double> rates;
}
