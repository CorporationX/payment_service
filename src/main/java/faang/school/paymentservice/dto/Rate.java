package faang.school.paymentservice.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class Rate {
    private boolean success;
    private long timestamp;
    private String base;
    private Date date;
    private Map<String, Double> rates;
}
