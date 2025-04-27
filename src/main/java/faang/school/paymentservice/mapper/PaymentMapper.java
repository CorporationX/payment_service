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
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    PaymentOperation toPaymentOperation(PaymentRequest paymentRequest);

    PaymentResponse toPaymentResponse(PaymentOperation paymentOperation);

    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    @Mapping(target = "operationId", source = "id")
    AuthorizationMessage toAuthorizationMessage(PaymentOperation paymentOperation);

    @Mapping(target = "operationId", source = "id")
    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    CancellationMessage toCancellationMessage(PaymentOperation paymentOperation);

    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    @Mapping(target = "operationId", source = "id")
    ClearingMessage toClearingMessage(PaymentOperation paymentOperation);
}
