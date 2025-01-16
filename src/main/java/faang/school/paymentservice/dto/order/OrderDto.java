package faang.school.paymentservice.dto.order;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import faang.school.paymentservice.dto.payment.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@JsonInclude(Include.NON_NULL)
public class OrderDto {
    private long id;
    private long userId;
    private String paymentMethod;
    private String paymentLink;
    private String servicePlan;
    private String serviceType;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
}
