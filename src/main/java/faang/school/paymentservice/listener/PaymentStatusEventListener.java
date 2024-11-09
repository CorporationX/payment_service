package faang.school.paymentservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.model.event.PaymentStatusEvent;
import faang.school.paymentservice.service.PaymentStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentStatusEventListener extends AbstractEventListener<PaymentStatusEvent> implements MessageListener {

    private final PaymentStatusService paymentStatusService;

    public PaymentStatusEventListener(ObjectMapper objectMapper,
                                      PaymentStatusService paymentStatusService) {
        super(objectMapper);
        this.paymentStatusService = paymentStatusService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        handleEvent(message, PaymentStatusEvent.class, event -> {
            //если отработало без ошибок
            if (event.getStatusDetails() == null) {
                switch (event.getStatus()) {
                    case IN_PROGRESS -> paymentStatusService.processInProgressStatus(event);
                    case COMPLETED -> paymentStatusService.processCompletedStatus(event);
                    case CANCELLED -> paymentStatusService.processCancelledStatus(event);
                    case FAILED -> paymentStatusService.processFailedStatus(event);
                        default -> throw new DataValidationException(String.format("Unknown request status: %s", event.getStatus()));
                }
                //если не прошло валидации, надо сохранить что-то в pending_operation и сообщить пользователю в чем проблема
            } else {
                //TODO update pending_operation
            }
            //TODO отправить в notification-service event через redis канал

        });
    }
}