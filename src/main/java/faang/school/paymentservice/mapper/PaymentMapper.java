package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.payment.PaymentDto;
import faang.school.paymentservice.dto.payment.PaymentCreateDto;
import faang.school.paymentservice.model.Payment;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentMapper {

    Payment toEntity(PaymentCreateDto dto);

    PaymentDto toPaymentDto(Payment entity);
}
