package faang.school.paymentservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
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
