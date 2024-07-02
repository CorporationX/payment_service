package faang.school.paymentservice.scheduler.payment.request;

import faang.school.paymentservice.model.Transaction;
import faang.school.paymentservice.repository.TransactionRepository;
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

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;

    @Scheduled(fixedRate = 5000)
    public void clearTransactions() {
        List<Transaction> transactions = transactionRepository.findReadyToClearTransactions();

        if (!transactions.isEmpty()) {
            for (Transaction transaction : transactions) {
                try {
                    transactionService.clearTransaction(transaction.getId());
                } catch (Exception e) {
                    log.error("Failed to clear transaction with ID {}: {}", transaction.getId(), e.getMessage());
                }
            }
        }
    }
}
