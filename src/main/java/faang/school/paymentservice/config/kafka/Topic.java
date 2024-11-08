package faang.school.paymentservice.config.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Topic {
    private String name;
    private int numPartitions;
    private short replicationFactor;
}
