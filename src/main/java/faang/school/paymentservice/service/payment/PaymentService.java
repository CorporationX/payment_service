package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.payment.PaymentDto;
import faang.school.paymentservice.dto.payment.PaymentDtoToCreate;
import org.springframework.stereotype.Component;

@Component
public interface PaymentService {

   Long createPayment(Long userId, PaymentDtoToCreate dto);

    PaymentDto getPayment(Long id);

    void cancelPayment(Long userId, Long paymentId);

    void clearPayment(Long paymentId);
}