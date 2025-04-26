package faang.school.paymentservice.dto.message;

import faang.school.paymentservice.dto.redis.RedisEvent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
public class AuthorizationMessage implements RedisEvent {
    @NotNull
    private UUID operationId;

    @NotNull
    private UUID sourceAccountId;

    @NotNull
    private UUID destinationAccountId;

    @NotNull
    @Min(1)
    private BigDecimal amount;

    @NotNull
    private String currency;

    @NotNull
    private Instant timestamp;

    @Override
    public String getChannelEvent() {
        return "";
    }
}
