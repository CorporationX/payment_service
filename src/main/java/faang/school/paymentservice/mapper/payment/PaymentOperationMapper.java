package faang.school.paymentservice.mapper.payment;

import faang.school.paymentservice.dto.payment.PaymentOperationAuthorizeRequestDto;
import faang.school.paymentservice.dto.payment.PaymentOperationResponseDto;
import faang.school.paymentservice.entity.payment.PaymentOperation;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface PaymentOperationMapper {
    PaymentOperation toPaymentOperationEntity(PaymentOperationAuthorizeRequestDto paymentOperationAuthorizeRequestDto);
    PaymentOperationResponseDto toPaymentOperationResponseDto(PaymentOperation paymentOperation);
}
