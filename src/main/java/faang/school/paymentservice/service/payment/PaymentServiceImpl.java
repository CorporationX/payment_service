package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.payment.PaymentDto;
import faang.school.paymentservice.dto.payment.PaymentDtoToCreate;
import faang.school.paymentservice.enums.PaymentStatus;
import faang.school.paymentservice.event.CancelPaymentEvent;
import faang.school.paymentservice.event.ClearPaymentEvent;
import faang.school.paymentservice.event.NewPaymentEvent;
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
import org.springframework.transaction.annotation.Propagation;
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
        UUID idempotencyKey = dto.getIdempotencyKey();
        log.info("Got idempotency key");

        try {
            Balance senderBalance = balanceRepository.findBalanceByAccountNumber(dto.getSenderAccountNumber())
                    .orElseThrow(() -> new NotFoundException("Sender balance hasn't been found"));

            balanceRepository.findBalanceByAccountNumber(dto.getReceiverAccountNumber())
                    .orElseThrow(() -> new NotFoundException("Receiver balance hasn't been found"));
            paymentValidator.validateNumbersAreDifferent(dto);
            paymentValidator.validateSenderHaveEnoughMoneyOnAuthorizationBalance(senderBalance, dto);

            Optional<Payment> optionalPayment = paymentRepository.findPaymentByIdempotencyKey(idempotencyKey);
            if (optionalPayment.isPresent()) {
                Payment payment = optionalPayment.get();
                paymentValidator.validatePaymentOnSameIdempotencyToken(dto, payment);
                log.info("Payment with UUID={} is idempotency and already been processed", idempotencyKey);
                return payment.getId();
            }

            Payment payment = paymentMapper.toEntity(dto);
            payment.setPaymentStatus(PaymentStatus.NEW);
            payment.setScheduledAt(LocalDateTime.now().plusHours(8));
            payment = paymentRepository.save(payment);
            log.info("Payment with UUID={} was saved in DB successfully", idempotencyKey);

            NewPaymentEvent event = new NewPaymentEvent(userId, payment.getId());
            newPaymentPublisher.publish(event);

            return payment.getId();
        } catch (Exception e) {
            log.error("Error occurred while creating payment: ", e);
            saveFailedPayment(dto);
            throw new PaymentException(e.getMessage());
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedPayment(PaymentDtoToCreate dto) {
        Payment failedPayment = paymentMapper.toEntity(dto);
        failedPayment.setPaymentStatus(PaymentStatus.FAILURE);
        paymentRepository.save(failedPayment);
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

        paymentValidator.validatePaymentStatusIsAlreadyCorrect(payment, PaymentStatus.CANCELED);

        CancelPaymentEvent event = new CancelPaymentEvent(userId, paymentId);

        cancelPaymentPublisher.publish(event);
    }

    @Transactional(readOnly = true)
    public void clearPayment(Long paymentId) {
        paymentRepository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("This payment doesn't exist"));

        ClearPaymentEvent event = new ClearPaymentEvent(paymentId);

        clearPaymentPublisher.publish(event);
    }
}