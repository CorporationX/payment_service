package faang.school.paymentservice.listener.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.model.PaymentStatus;
import faang.school.paymentservice.service.payment.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;

import static faang.school.paymentservice.model.PaymentStatus.FORCED_SUCCESS;

@Slf4j
@Component
public class ForcedEventResponseListener extends AbstractEventResponseListener {
    public ForcedEventResponseListener(ObjectMapper objectMapper, PaymentService paymentService) {
        super(objectMapper, paymentService);
    }

    @Override
    protected Set<PaymentStatus> getAllowedStatuses() {
        return Set.of(FORCED_SUCCESS);
    }
}

