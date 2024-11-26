package faang.school.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CourseDto {

    private String currencySent;
    private String currencyReceived;
    private double rate;
}

