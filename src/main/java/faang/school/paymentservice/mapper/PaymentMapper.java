package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.PaymentDto;
import faang.school.paymentservice.dto.event.payment.PaymentEvent;
import faang.school.paymentservice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {
    Payment toEntity(PaymentDto dto);

    PaymentDto toDto(Payment entity);

    @Mapping(target = "type", source = "status")
    PaymentEvent toEvent(Payment payment);
}