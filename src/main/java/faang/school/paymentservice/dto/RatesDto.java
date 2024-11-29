package faang.school.paymentservice.dto;

import lombok.Data;

import java.util.Map;

@Data
public class RatesDto {

    public String base;

    public Map<String, Double> rates;
}
