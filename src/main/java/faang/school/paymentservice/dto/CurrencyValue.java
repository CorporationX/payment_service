package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CurrencyValue {

    @JsonProperty("CharCode")
    private String charCode;

    @JsonProperty("Value")
    private double value;
}