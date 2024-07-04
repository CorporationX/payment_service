package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.payment.PaymentDto;
import faang.school.paymentservice.dto.payment.PaymentDtoToCreate;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.event.CancelPaymentEvent;
import faang.school.paymentservice.event.ClearPaymentEvent;
import faang.school.paymentservice.event.NewPaymentEvent;
import faang.school.paymentservice.exception.IdempotencyException;
import faang.school.paymentservice.exception.NotEnoughMoneyOnBalanceException;
import faang.school.paymentservice.exception.NotFoundException;
import faang.school.paymentservice.exception.PaymentException;
import faang.school.paymentservice.mapper.PaymentMapper;
import faang.school.paymentservice.model.Balance;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.publisher.CancelPaymentPublisher;
import faang.school.paymentservice.publisher.ClearPaymentPublisher;
import faang.school.paymentservice.publisher.NewPaymentPublisher;
import faang.school.paymentservice.repository.BalanceRepository;
import faang.school.paymentservice.repository.PaymentRepository;
import faang.school.paymentservice.validator.payment.PaymentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final NewPaymentPublisher newPaymentPublisher;
    private final CancelPaymentPublisher cancelPaymentPublisher;
    private final ClearPaymentPublisher clearPaymentPublisher;
    private final PaymentValidator paymentValidator;

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

        NewPaymentEvent event = new NewPaymentEvent(payment.getId());

        newPaymentPublisher.publish(event);

        return payment.getId();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentDto getPayment(Long id) {
        return paymentMapper.toDto(paymentRepository.findById(id).orElseThrow(() -> new NotFoundException(
                String.format("Not found payment with id %d", id))));
    }

    @Override
    @Transactional(readOnly = true)
    public void cancelPayment(Long userId, Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("This payment doesn't exist"));

        if (payment.getPaymentStatus() != PaymentStatus.NEW &&
            payment.getPaymentStatus() != PaymentStatus.READY_TO_CLEAR) {
            throw new PaymentException("It is impossible to perform this operation with this payment, since it has already been closed");
        }

        CancelPaymentEvent event = new CancelPaymentEvent(paymentId);

        cancelPaymentPublisher.publish(event);
        log.info("Payment with ID={} has been processed and is ready to clear", paymentId);
    }

    @Transactional(readOnly = true)
    public void clearPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("This payment doesn't exist"));

        if (payment.getPaymentStatus() != PaymentStatus.READY_TO_CLEAR) {
            throw new PaymentException("It is impossible to perform this operation with this payment, since it has already been closed");
        }

        ClearPaymentEvent event = new ClearPaymentEvent(paymentId);

        clearPaymentPublisher.publish(event);
        log.info("Payment with ID={} has been processed and is ready to clear", paymentId);
    }

    private boolean checkPaymentWithSameUUID(PaymentDtoToCreate newPayment, Payment oldPayment) {
        return newPayment.getSenderAccountNumber().equals(oldPayment.getSenderAccountNumber())
               && newPayment.getReceiverAccountNumber().equals(oldPayment.getReceiverAccountNumber())
               && newPayment.getAmount().compareTo(oldPayment.getAmount()) == 0
               && newPayment.getCurrency() == oldPayment.getCurrency();
    }
}