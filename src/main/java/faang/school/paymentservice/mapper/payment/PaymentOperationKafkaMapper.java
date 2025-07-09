package faang.school.paymentservice.mapper.payment;

import faang.school.paymentservice.entity.payment.PaymentOperation;
import faang.school.paymentservice.event.payment.PaymentAuthorizationEventDto;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface PaymentOperationKafkaMapper {
    PaymentAuthorizationEventDto toPaymentAuthorizationEventDto(PaymentOperation paymentOperation);
}
