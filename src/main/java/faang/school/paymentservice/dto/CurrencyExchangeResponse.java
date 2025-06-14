package faang.school.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;

@Data
@AllArgsConstructor
public class CurrencyExchangeResponse {
    private String base;
    private HashMap<String, Double> rates;
}
