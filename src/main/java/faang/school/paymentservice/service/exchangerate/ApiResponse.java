package faang.school.paymentservice.service.exchangerate;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class ApiResponse {
    private Map<String, Double> rates;
}
