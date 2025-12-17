package faang.school.paymentservice.service.transaction;

import faang.school.paymentservice.dto.TypeOperation;
import faang.school.paymentservice.model.Transfer;
import faang.school.paymentservice.model.Transaction;
import faang.school.paymentservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public void saveTransfersTransaction(Transfer transfer, TypeOperation typeOperation) {
        Transaction transaction = Transaction.builder()
                .transfer(transfer)
                .typeOperation(typeOperation)
                .status(transfer.getStatus())
                .statusDescription(transfer.getStatusDescription())
                .build();
        transactionRepository.save(transaction);
    }
}
