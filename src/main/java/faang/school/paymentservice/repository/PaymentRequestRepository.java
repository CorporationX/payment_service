package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.PaymentRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentRequestRepository {
    private final PaymentRequestJpaRepository repository;

    public List<PaymentRequest> findPendingPaymentRequests() {
        return repository.findPaymentRequestsByIsClearedIsFalse();
    }

    public PaymentRequest save(PaymentRequest paymentRequest) {
        return repository.save(paymentRequest);
    }

    public PaymentRequest findById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(() -> {
                    String message = String.format("There is no any payment requests with number %s.", id);
                    throw new EntityNotFoundException(message);
                });
    }
}
