package faang.school.paymentservice.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class RateResponse {
    private boolean success;
    private long timestamp;
    private String base;
    private Date date;
    private List<String> rates;
}
