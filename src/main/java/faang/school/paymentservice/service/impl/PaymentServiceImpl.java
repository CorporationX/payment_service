package faang.school.paymentservice.service.impl;

import faang.school.paymentservice.client.AccountServiceClient;
import faang.school.paymentservice.config.context.UserContext;
import faang.school.paymentservice.exception.OperationNotPermittedException;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.dto.AccountDto;
import faang.school.paymentservice.model.dto.PaymentDto;
import faang.school.paymentservice.model.entity.Payment;
import faang.school.paymentservice.model.enums.PaymentStatus;
import faang.school.paymentservice.repository.PendingOperationRepository;
import faang.school.paymentservice.service.PaymentService;
import faang.school.paymentservice.validator.ValidatorPaymentService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PendingOperationRepository pendingOperationRepository;
    private final ValidatorPaymentService validatorPaymentService;
    private final PaymentMapper paymentMapper;
    private final AccountServiceClient accountServiceClient;
    private final UserContext userContext;

    @Override
    @Transactional
    public Long createPayment(PaymentDto paymentDto) {
        AccountDto accountDto = accountServiceClient.getAccountNumber(paymentDto.getOwnerAccountNumber()).getBody();
        validatorPaymentService.ownerNumberCorrect(paymentDto, accountDto, userContext);
        Optional<Payment> payment = pendingOperationRepository.findByIdempotencyToken(paymentDto.getIdempotencyToken().toString());
        if (payment.isPresent()) {
            if (validatorPaymentService.operationExist(payment.get(), paymentDto)) {
                return payment.get().getId();
            }
        }
        paymentDto.setStatus(PaymentStatus.PENDING);
        Payment paymentSave = pendingOperationRepository.save(paymentMapper.toEntity(paymentDto));

        return paymentSave.getId();
        // TODO
    }

    @Override
    @Transactional
    public void cancelPayment(Long pendingOperationId) {
        Payment payment = pendingOperationRepository.findById(pendingOperationId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Payment with ID %d not found", pendingOperationId))
        );
        AccountDto accountDto = accountServiceClient.getAccountNumber(payment.getOwnerAccountNumber()).getBody();
        Set<PaymentStatus> allowedStatus = EnumSet.of(PaymentStatus.PENDING, PaymentStatus.IN_PROGRESS);
        if (accountDto.getUserId() == userContext.getUserId() && allowedStatus.contains(payment.getStatus())) {
            payment.setStatus(PaymentStatus.CANCELED);
        } else {
            throw new OperationNotPermittedException(String.format("Operation prohibited, this userId %d " +
                            "does not match the number %s in the operation",
                    userContext.getUserId(), payment.getOwnerAccountNumber()));
        }
        // TODO
    }

    @Override
    public PaymentDto getPayment(Long paymentId) {
        Payment payment = pendingOperationRepository.findById(paymentId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Payment with ID %d not found", paymentId)));
        AccountDto accountDto = accountServiceClient.getAccountNumber(payment.getOwnerAccountNumber()).getBody();
        if (accountDto.getUserId() == userContext.getUserId()) {
            return paymentMapper.toDto(payment);
        } else {
            throw new OperationNotPermittedException(String.format("Operation prohibited, this userId %d " +
                            "does not match the number %s in the operation",
                    userContext.getUserId(), payment.getOwnerAccountNumber()));
        }
    }
}
