package faang.school.paymentservice.mapper;

import faang.school.paymentservice.dto.CurrencyRate;
import faang.school.paymentservice.entity.CurrencyRateDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CurrencyRateMapper {
    @Mapping(target = "timestamp", qualifiedByName = "mapTimestampToLocalDateTime")
    CurrencyRate toEntity(CurrencyRateDto dto);

    @Named("mapTimestampToLocalDateTime")
    default LocalDateTime mapTimestampToLocalDateTime(Long timestamp) {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
