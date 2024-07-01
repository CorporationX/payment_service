package faang.school.paymentservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Random;

@Data
@Builder
public class PaymentResponse {
    private PaymentStatus status;
    private int verificationCode;
    private Long debitAccountId;
    private Long creditAccountId;
    private BigDecimal amount;
    private Currency currency;
    private String message;

    public static int generateVerificationCode() {
        return new Random().nextInt(1000, 9999);
    }
}
