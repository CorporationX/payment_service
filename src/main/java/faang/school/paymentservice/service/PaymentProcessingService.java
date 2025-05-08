package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentOperationRequest;
import faang.school.paymentservice.mapper.PaymentOperationMapper;
import faang.school.paymentservice.repository.PaymentProcessingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessingService {

    private final PaymentProcessingRepository paymentProcessingRepository;
    private final PaymentOperationMapper paymentOperationMapper;

    public void initPayment(PaymentOperationRequest request) {

    }

    public void cancelPayment(Long paymentId) {

    }

    public void confirmPaymentForced(Long paymentId) {

    }

    @Async("paymentClearing")
    public void findPaymentsReadyToConfirmed() {

    }
}
