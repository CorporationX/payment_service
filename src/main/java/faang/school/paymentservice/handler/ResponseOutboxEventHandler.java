package faang.school.paymentservice.handler;

import faang.school.paymentservice.dto.event.ResponseOutboxEvent;
import faang.school.paymentservice.service.PaymentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ResponseOutboxEventHandler {

    private final PaymentProcessingService paymentProcessingService;

    public void handle(ResponseOutboxEvent event) {

    }
}
