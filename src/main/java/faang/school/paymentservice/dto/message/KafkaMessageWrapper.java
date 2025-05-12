package faang.school.paymentservice.dto.message;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KafkaMessageWrapper {
    private PaymentOperationMessage paymentOperationMessage;
    private String topic;
}
