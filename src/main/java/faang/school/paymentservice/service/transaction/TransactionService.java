package faang.school.paymentservice.service.transaction;

import faang.school.paymentservice.model.BankOperation;
import faang.school.paymentservice.model.Transaction;
import faang.school.paymentservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public void saveTransactionBankOperation(BankOperation bankOperation) {
        Transaction transaction = Transaction.builder()
                .bankOperation(bankOperation)
                .requestAccountId(bankOperation.getSenderAccountId())
                .typeOperation(bankOperation.getTypeOperation())
                .status(bankOperation.getStatus())
                .statusDescription(bankOperation.getStatusDescription())
                .build();
        transactionRepository.save(transaction);
    }
}
