package faang.school.paymentservice.service;

import faang.school.paymentservice.model.dto.PaymentDto;

public interface PaymentService {
    Long createPayment(PaymentDto paymentDto);
    void cancelPayment(Long pendingOperationId);
    PaymentDto getPayment(Long paymentId);
}
