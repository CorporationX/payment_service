package faang.school.paymentservice.mapper;

import faang.school.paymentservice.model.dto.PaymentDto;
import faang.school.paymentservice.model.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    Payment toEntity(PaymentDto paymentDto);

    PaymentDto toDto(Payment payment);
}
