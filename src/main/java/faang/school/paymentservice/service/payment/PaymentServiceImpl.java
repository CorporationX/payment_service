package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.client.service.AccountServiceClient;
import faang.school.paymentservice.dto.payment.PaymentDto;
import faang.school.paymentservice.dto.payment.PaymentDtoToCreate;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.exception.IdempotencyException;
import faang.school.paymentservice.exception.NotEnoughMoneyOnBalanceException;
import faang.school.paymentservice.exception.NotFoundException;
import faang.school.paymentservice.exception.PaymentException;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.Balance;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.repository.BalanceRepository;
import faang.school.paymentservice.repository.PaymentRepository;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BalanceRepository balanceRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final AccountServiceClient accountService;

    @Override
    @Transactional
    public Long createPayment(Long userId, PaymentDtoToCreate dto) {
        Payment payment = new Payment();
        UUID idempotencyKey = dto.getIdempotencyKey();

        Balance senderBalance = balanceRepository.findBalanceByAccountNumber(payment.getSenderAccountNumber())
                .orElseThrow(() -> new NotFoundException("Sender balance hasn't been found"));

        if (senderBalance.getAuthorizationBalance().compareTo(payment.getAmount()) < 0) {
            throw new NotEnoughMoneyOnBalanceException("Not enough money");
        }

        Optional<Payment> optionalPayment = paymentRepository.findPaymentByIdempotencyKey(idempotencyKey);
        if (optionalPayment.isPresent()) {
            payment = optionalPayment.get();
            boolean isIdempotency = checkPaymentWithSameUUID(dto, payment);
            if (!isIdempotency) {
                throw new IdempotencyException("This payment has already been made with other details! Try again!");
            }
            log.info("Payment with UUID={} is idempotency and already been processed", idempotencyKey);
            return payment.getId();
        }

        dto.setPaymentStatus(PaymentStatus.NEW);
        dto.setScheduledAt(LocalDateTime.now().plusHours(8));
        payment = paymentRepository.save(paymentMapper.toEntity(dto));
        log.info("Payment with UUID={} was saved in DB successfully", idempotencyKey);

        try {
            accountService.authorizePayment(payment.getId());
            log.info("New payment, UUID={}, has been posted to account-service", dto.getIdempotencyKey());
        } catch (FeignException e) {
            throw e;
        }
        return payment.getId();
    }

    @Override
    public PaymentDto getPayment(Long id) {
        return paymentMapper.toDto(paymentRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Not found payment with id %d", id))));
    }

    @Override
    @Transactional
    public void cancelPayment(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("This payment doesn't exist"));

        if (payment.getPaymentStatus() != PaymentStatus.NEW &&
                payment.getPaymentStatus() != PaymentStatus.READY_TO_CLEAR) {
            throw new PaymentException("It is impossible to perform this operation with this payment, since it has already been closed");
        }

        try {
            accountService.cancelPayment(userId, paymentId);
            log.info("Payment with UUID={}, has been canceled in account-service", payment.getIdempotencyKey());
        } catch (FeignException e) {
            throw e;
        }
    }

    @Transactional
    public void clearPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("This payment doesn't exist"));

        if (payment.getPaymentStatus() != PaymentStatus.READY_TO_CLEAR){
            throw new PaymentException("It is impossible to perform this operation with this payment, since it has already been closed");
        }

        try {
            accountService.clearPayment(paymentId);
            log.info("Payment with UUID={}, has been canceled in account-service", payment.getIdempotencyKey());
        } catch (FeignException e) {
            throw e;
        }
    }

    private boolean checkPaymentWithSameUUID(PaymentDtoToCreate newPayment, Payment oldPayment) {
        return newPayment.getSenderAccountNumber().equals(oldPayment.getSenderAccountNumber())
                && newPayment.getReceiverAccountNumber().equals(oldPayment.getReceiverAccountNumber())
                && newPayment.getAmount().compareTo(oldPayment.getAmount()) == 0
                && newPayment.getCurrency() == oldPayment.getCurrency();
    }
}