package faang.school.paymentservice.dto.payment;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthorizationResponse {
    private Long requestId;
    private String verificationCode;
}
