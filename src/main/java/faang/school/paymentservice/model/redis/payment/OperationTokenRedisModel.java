package faang.school.paymentservice.model.redis.payment;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("operation_token")
public class OperationTokenRedisModel implements Serializable {
    @Id
    private UUID key;

    @TimeToLive
    private Long ttl;

    private LocalDateTime createdAt;
}
