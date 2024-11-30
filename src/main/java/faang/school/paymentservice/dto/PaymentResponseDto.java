package faang.school.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto {
    PaymentStatus status;
    int verificationCode;
    long paymentNumber;
    BigDecimal amount;
    Currency currency;
    String message;
}
