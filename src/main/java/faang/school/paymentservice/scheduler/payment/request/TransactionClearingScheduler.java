package faang.school.paymentservice.scheduler.payment.request;

import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.repository.PaymentRepository;
import faang.school.paymentservice.service.transaction.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionClearingScheduler {

    private final PaymentRepository paymentRepository;
    private final TransactionService transactionService;

    @Scheduled(fixedRate = 5000)
    public void clearTransactions() {
        List<Payment> payments = paymentRepository.findReadyToClearTransactions();

        if (!payments.isEmpty()) {
            for (Payment payment : payments) {
                try {
                    transactionService.clearTransaction(payment.getId());
                } catch (Exception e) {
                    log.error("Failed to clear transaction with ID {}: {}", payment.getId(), e.getMessage());
                }
            }
        }
    }
}
