package faang.school.paymentservice.job.payment;

import faang.school.paymentservice.facade.payment.PaymentOperationKafkaPublisherFacade;
import faang.school.paymentservice.service.payment.PaymentOperationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.stream.IntStream;

@Component
@Slf4j
public class JobPaymentOperationClearing {
    private final PaymentOperationService paymentOperationService;
    private final PaymentOperationKafkaPublisherFacade paymentOperationKafkaPublisherFacade;
    private final Executor executor;
    @Value("${jobs.payment-operation.clearing.threads}")
    private int numThreads;

    public JobPaymentOperationClearing(PaymentOperationService paymentOperationService,
                                       PaymentOperationKafkaPublisherFacade paymentOperationKafkaPublisherFacade,
                                       @Qualifier("clearingPaymentOperationExecutor") Executor executor) {
        this.paymentOperationService = paymentOperationService;
        this.paymentOperationKafkaPublisherFacade = paymentOperationKafkaPublisherFacade;
        this.executor = executor;
    }

    @Scheduled(cron = "${jobs.payment-operation.clearing.cron}")
    public void clearPaymentOperation() {
        IntStream.range(0, numThreads).forEach(value -> executor.execute(this::processOneOperation));
    }

    private void processOneOperation() {
        paymentOperationService.getOperationForClearing().ifPresent(operation -> {
            paymentOperationKafkaPublisherFacade.sendMessagePaymentClearing(operation);
            log.info("Operation {} sent for clearing", operation);
        });
    }
}
