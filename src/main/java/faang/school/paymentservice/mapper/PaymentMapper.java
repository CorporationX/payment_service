package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.message.AuthorizationMessage;
import faang.school.paymentservice.dto.message.CancellationMessage;
import faang.school.paymentservice.dto.message.ClearingMessage;
import faang.school.paymentservice.model.PaymentOperation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    @Mapping(target = "authorizationId", ignore = true)
    @Mapping(target = "clearScheduledAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    PaymentOperation toPaymentOperation(PaymentRequest paymentRequest);

    PaymentResponse toPaymentResponse(PaymentOperation paymentOperation);

    AuthorizationMessage toAuthorizationMessage(PaymentOperation paymentOperation);

    CancellationMessage toCancellationMessage(PaymentOperation paymentOperation);

    ClearingMessage toClearingMessage(PaymentOperation paymentOperation);

}
