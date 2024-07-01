package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.model.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentRequestMapper {
    PaymentRequest toEntity(PaymentRequestDto dto);
}
