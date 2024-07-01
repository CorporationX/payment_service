package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.event.PaymentEventDto;
import faang.school.paymentservice.model.OperationType;
import faang.school.paymentservice.model.PaymentRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentRequestMapper {
    PaymentRequest toModel(PaymentRequestDto dto);

    PaymentResponse toPaymentResponse(PaymentRequest pendingRequest);

    @Mapping(target = "currency", source = "currency", qualifiedByName = "getCurrencyName")
    @Mapping(target = "type", source = "type", qualifiedByName = "getOperationTypeName")
    PaymentEventDto toPaymentEvent(PaymentRequest pendingRequest);

    @Named("getCurrencyName")
    default String getCurrencyName(Currency currency) {
        return currency.name();
    }

    @Named("getOperationTypeName")
    default String getOperationTypeName(OperationType type) {
        return type.name();
    }
}
