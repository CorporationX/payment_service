package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.mapper.PaymentRequestMapper;
import faang.school.paymentservice.model.OperationType;
import faang.school.paymentservice.model.PaymentRequest;
import faang.school.paymentservice.repository.PaymentRequestJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final long CLEARING_DELAY = 15;
    private static final TemporalUnit CLEARING_DELAY_TEMPORAL_UNIT = ChronoUnit.MINUTES;
    private final PaymentRequestJpaRepository repository;
    private final PaymentRequestMapper paymentRequestMapper;

    @Transactional
    public PaymentResponse sendPayment(PaymentRequestDto dto) {
        PaymentRequest pendingRequest = repository.save(createPendingRequest(dto));


        //Кидаем сообщение в топик редиса

        //Настраиваем джобу для автоклиринга

        //Формируем и возвращаем ответ
        return null;
    }

    private PaymentRequest createPendingRequest(PaymentRequestDto dto) {
        PaymentRequest pendingRequest = paymentRequestMapper.toEntity(dto);
        pendingRequest.setType(OperationType.AUTHORIZATION);
        pendingRequest.setIsCleared(false);
        pendingRequest.setClearScheduledAt(LocalDateTime.now().plus(CLEARING_DELAY, CLEARING_DELAY_TEMPORAL_UNIT));

        return pendingRequest;
    }
}
