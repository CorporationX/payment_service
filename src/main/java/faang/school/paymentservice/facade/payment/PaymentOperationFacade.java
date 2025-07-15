package faang.school.paymentservice.facade.payment;

import faang.school.paymentservice.dto.payment.PaymentOperationAuthorizeRequestDto;
import faang.school.paymentservice.dto.payment.PaymentOperationResponseDto;
import faang.school.paymentservice.entity.payment.PaymentOperation;
import faang.school.paymentservice.mapper.payment.PaymentOperationMapper;
import faang.school.paymentservice.service.payment.PaymentOperationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentOperationFacade {
    private final PaymentOperationService paymentOperationService;
    private final PaymentOperationMapper paymentOperationMapper;
    private final PaymentOperationKafkaPublisherFacade paymentOperationKafkaPublisherFacade;

    public PaymentOperationResponseDto getPaymentOperationById(UUID paymentOperationId) {
        PaymentOperation paymentOperation = paymentOperationService.getPaymentOperationById(paymentOperationId);

        PaymentOperationResponseDto responseDto =
                paymentOperationMapper.toPaymentOperationResponseDto(paymentOperation);
        log.info("Mapping PaymentOperation entity to PaymentOperationResponseDto." +
                "Entity content: {}. DTO content: {}.", paymentOperation, responseDto);

        return responseDto;
    }

    public PaymentOperationResponseDto authorizePayment(PaymentOperationAuthorizeRequestDto requestDto) {
        PaymentOperation paymentOperation = paymentOperationMapper.toPaymentOperationEntity(requestDto);
        log.info("Mapping PaymentOperationAuthorizeRequestDto to PaymentOperation entity." +
                        "DTO content: {}. Entity content: {}.", requestDto, paymentOperation);

        paymentOperation = paymentOperationService.authorizePayment(paymentOperation);

        PaymentOperationResponseDto responseDto =
                paymentOperationMapper.toPaymentOperationResponseDto(paymentOperation);
        log.info("Mapping PaymentOperation entity to PaymentOperationResponseDto." +
                        "Entity content: {}. DTO content: {}.", paymentOperation, responseDto);

        if (!paymentOperation.isWasAlreadyPresent()) {
            paymentOperationKafkaPublisherFacade.sendMessagePaymentAuthorization(paymentOperation);
        }

        return responseDto;
    }

    public PaymentOperationResponseDto clearPayment(UUID paymentOperationId) {
        PaymentOperation paymentOperation = paymentOperationService.cancelClearing(paymentOperationId);

        PaymentOperationResponseDto responseDto =
                paymentOperationMapper.toPaymentOperationResponseDto(paymentOperation);
        log.info("Mapping PaymentOperation entity to PaymentOperationResponseDto." +
                "Entity content: {}. DTO content: {}.", paymentOperation, responseDto);

        paymentOperationKafkaPublisherFacade.sendMessagePaymentClearing(paymentOperation);

        return responseDto;
    }

    public PaymentOperationResponseDto cancelPayment(UUID paymentOperationId) {
        PaymentOperation paymentOperation = paymentOperationService.cancelClearing(paymentOperationId);

        PaymentOperationResponseDto responseDto =
                paymentOperationMapper.toPaymentOperationResponseDto(paymentOperation);
        log.info("Mapping PaymentOperation entity to PaymentOperationResponseDto." +
                "Entity content: {}. DTO content: {}.", paymentOperation, responseDto);

        paymentOperationKafkaPublisherFacade.sendMessagePaymentCancel(paymentOperation);

        return responseDto;
    }
}
