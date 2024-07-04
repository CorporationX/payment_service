package faang.school.paymentservice.scheduler.payment;

import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.repository.PaymentRepository;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentClearingScheduler {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

//    @Scheduled(fixedRate = 5000)
    public void clearPayments() {
        List<Payment> payments = paymentRepository.findReadyToClearTransactions();

        if (!payments.isEmpty()) {
            for (Payment payment : payments) {
                try {
                    paymentService.clearPayment(payment.getId());
                } catch (Exception e) {
                    log.error("Failed to clear payment with ID {}: {}", payment.getId(), e.getMessage());
                }
            }
        }
    }
}
