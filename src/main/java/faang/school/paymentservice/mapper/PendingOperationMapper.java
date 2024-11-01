package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PendingOperationDto;
import faang.school.paymentservice.model.PendingOperation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PendingOperationMapper {
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "accountBalanceStatus", constant = "BALANCE_NOT_VERIFIED")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "currency", expression = "java(convertCurrency(dto.getCurrency()))")
    PendingOperation toEntity(PendingOperationDto dto);

    PendingOperationDto toResponseDto(PendingOperation entity);

    default Currency convertCurrency(String currency) {
        try {
            return Currency.valueOf(currency);
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Invalid or missing currency value: " + currency);
        }
    }
}
