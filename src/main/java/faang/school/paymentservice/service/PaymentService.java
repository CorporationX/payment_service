package faang.school.paymentservice.service;

import faang.school.paymentservice.client.AccountServiceClient;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.entity.Pending;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.publisher.PaymentEventPublisher;
import faang.school.paymentservice.repository.PendingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PendingRepository pendingRepository;
    private final PaymentMapper paymentMapper;
//    private final PaymentEventPublisher paymentEventPublisher;
    private final AccountServiceClient accountServiceClient;

    public PaymentResponse createNewPayment(PaymentRequest dto) {
        if(pendingRepository.existsByIdempotencyToken(dto.idempotencyToken())){
            throw new IllegalStateException("payment with this token already exist");
        }

        Pending pending = paymentMapper.toEntity(dto);
        pending.setStatus(accountServiceClient.reserveMoney(dto.ownerAccountNumber(),dto.amount()));

        pendingRepository.save(pending);
//        paymentEventPublisher.publish(pending);
        return createResponse(pending,"successful authorization payment");
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
