package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;

public interface ConvertService {
    PaymentResponse getConvertedPayment(PaymentRequest dto);
}
