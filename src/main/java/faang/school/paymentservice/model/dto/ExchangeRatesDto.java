package faang.school.paymentservice.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class ExchangeRatesDto {
    private String success;
    private Long timestamp;
    private String base;
    private Date date;
    private Map<String, String> rates;
}
