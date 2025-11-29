package faang.school.paymentservice.job.payment;

import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.repository.BankOperationRepository;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class PaymentSchedulerService {

    private final BankOperationRepository bankOperationRepository;
    private final PaymentService paymentService;

    public void clearingSchedulingPayments() {
        List<UUID> bankOperationIdList = bankOperationRepository.findIdsByStatusAndClearScheduledAt(
                PaymentStatus.AUTHORIZATION_SUCCESS,
                LocalDateTime.now());
        bankOperationIdList.forEach(paymentService::clearingOperation);
    }

    public void retryAuthorizationErrorPayments() {
        List<UUID> bankOperationIdList = bankOperationRepository.findIdsByStatus(PaymentStatus.AUTHORIZATION_ERROR);
        bankOperationIdList.forEach(paymentService::retryAuthorization);
    }

    public void retryClearingErrorPayments() {
        List<UUID> bankOperationIdList = bankOperationRepository.findIdsByStatus(PaymentStatus.CLEARING_ERROR);
        bankOperationIdList.forEach(paymentService::clearingOperation);
    }

    public void retryCancelErrorPayments() {
        List<UUID> bankOperationIdList = bankOperationRepository.findIdsByStatus(PaymentStatus.CANCEL_ERROR);
        bankOperationIdList.forEach(paymentService::retryAuthorization);
    }
}
