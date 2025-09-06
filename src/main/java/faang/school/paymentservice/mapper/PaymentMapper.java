package faang.school.paymentservice.mapper;

import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.dto.PaymentRequestDto;
import faang.school.paymentservice.model.dto.PaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "idempotencyToken", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Payment toPayment(PaymentRequestDto request);

    PaymentResponseDto toResponse(Payment payment);
}