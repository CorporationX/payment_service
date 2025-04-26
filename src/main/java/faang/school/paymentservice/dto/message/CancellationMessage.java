package faang.school.paymentservice.dto.message;

import faang.school.paymentservice.dto.redis.RedisEvent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class CancellationMessage implements RedisEvent {
    @NotNull
    private UUID operationId;

    @NotNull
    private UUID authorizationId;

    @NotNull
    private Instant timestamp;

    @Override
    public String getChannelEvent() {
        return "";
    }
}
