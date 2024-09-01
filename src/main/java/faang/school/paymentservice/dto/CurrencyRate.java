package faang.school.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

/**
 * @author Evgenii Malkov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRate {
    long timestamp;
    String base;
    LocalDate date;
    Map<String, BigDecimal> rates;
}
