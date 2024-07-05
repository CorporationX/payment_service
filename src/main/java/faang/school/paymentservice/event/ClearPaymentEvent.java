package faang.school.paymentservice.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClearPaymentEvent implements Event {
    @JsonProperty("paymentId")
    private Long paymentId;
}
