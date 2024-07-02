package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.payment.PaymentDto;
import faang.school.paymentservice.dto.payment.PaymentDtoToCreate;
import faang.school.paymentservice.model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    Payment toEntity(PaymentDtoToCreate dto);

    PaymentDto toDto(Payment entity);
}