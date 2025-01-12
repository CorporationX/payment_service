package faang.school.paymentservice.service;

import com.atomikos.icatch.jta.UserTransactionManager;
import faang.school.paymentservice.client.AccountServiceClient;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.entity.Pending;
import faang.school.paymentservice.event.AuthorizationMessageEvent;
import faang.school.paymentservice.event.AuthorizationMessageResultEvent;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.publisher.PaymentEventPublisher;
import faang.school.paymentservice.repository.PendingRepository;
import jakarta.transaction.Transactional;
import jakarta.transaction.UserTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PendingRepository pendingRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentEventPublisher paymentEventPublisher;
    private final AccountServiceClient accountServiceClient;
    private final UserTransactionManager userTransactionManager;


    public PaymentResponse createNewPayment(PaymentRequest dto) {

        if(pendingRepository.existsByIdempotencyToken(dto.idempotencyToken())){
            throw new IllegalStateException("payment with this token already exist");
        }

        Pending pending = paymentMapper.toEntity(dto);
        pending.setStatus(PaymentStatus.CREATE);

        pendingRepository.save(pending);

        paymentEventPublisher.publish(new AuthorizationMessageEvent(
                pending.getOwnerAccountNumber(),
                pending.getAmount(),
                pending.getIdempotencyToken()));

        return createResponse(pending,"successful authorization payment");
    }
    @Transactional
    public void ChangeStatus(AuthorizationMessageResultEvent event){
        Pending pending = pendingRepository.findByIdempotencyToken(event.getIdempotencyToken());
        pending.setStatus(event.getPaymentStatus());
    }
    @Retryable
    public PaymentStatus getStatus(String idempotencyToken){
        return pendingRepository.findByIdempotencyToken(idempotencyToken).getStatus();
    }

    public PaymentResponse cleanPayment(String idempotencyToken, PaymentStatus paymentStatus){
        if(paymentStatus!=PaymentStatus.CLOSED&&paymentStatus!=PaymentStatus.SUCCESS){
            throw new IllegalArgumentException("Illegal status for payment");
        }


    }

    private PaymentResponse createResponse(Pending pending, String message){
    return new PaymentResponse(
            pending.getStatus(),
            pending.getIdempotencyToken(),
            pending.getReceiverAccountNumber(),
            pending.getAmount(),
            pending.getCurrency(),
            message);
    }
}
