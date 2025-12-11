package faang.school.paymentservice.job.payment;

import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.TransferRepository;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentSchedulerService {

    private final TransferRepository transferRepository;
    private final PaymentService paymentService;

    public void clearingSchedulingPayments() {
        List<UUID> bankOperationIdList = transferRepository.findIdsByStatusAndClearScheduledAt(
                PaymentStatus.AUTHORIZATION_SUCCESS,
                LocalDateTime.now());
        bankOperationIdList.forEach(paymentService::clearingOperation);
    }
}
