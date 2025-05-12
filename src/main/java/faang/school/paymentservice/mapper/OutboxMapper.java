package faang.school.paymentservice.mapper;

import faang.school.paymentservice.model.OutboxEvent;
import faang.school.paymentservice.model.PaymentOperation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;


@Mapper(componentModel = "spring")
public interface OutboxMapper {

    @Mapping(target = "sentAt", ignore = true)
    @Mapping(target = "id", qualifiedByName = "generateId")
    @Mapping(target = "paymentOperationId", source = "paymentOperation.id")
    @Mapping(target = "eventType", source = "paymentOperation.paymentStatus")
    @Mapping(target = "payload", ignore = true)
    @Mapping(target = "outboxStatus", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.OffsetDateTime.now())")
    OutboxEvent toOutboxEvent(PaymentOperation paymentOperation);

    @Named("generateId")
    default UUID generateId(Object ignored) {
        return UUID.randomUUID();
    }
}