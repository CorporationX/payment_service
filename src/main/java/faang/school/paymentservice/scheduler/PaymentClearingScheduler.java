package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.model.PaymentOperation;
import faang.school.paymentservice.repository.PaymentOperationRepository;
import faang.school.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
/**
 * Планировщик для автоматического клиринга (проведения) платежей по расписанию.
 * <p>
 * Основные функции:
 * <ul>
 *   <li>Поиск платежей, готовых к клирингу (со статусом PENDING и прошедшей датой clearScheduledAt)</li>
 *   <li>Принудительное проведение найденных платежей через {@link PaymentService#forcedPayment}</li>
 * </ul>
 *
 * <p>Расписание работы задается в конфигурации свойством:
 * <ul>
 *   <li><code>scheduler.clearing.cron</code> - cron-выражение для настройки расписания</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
public class PaymentClearingScheduler {
    private final PaymentOperationRepository repository;
    private final PaymentService paymentService;

    @Scheduled(cron = "${scheduler.clearing.cron}")
    public void processScheduledClearing() {
        Instant now = Instant.now();
        List<PaymentOperation> operations =
                repository.findByPaymentStatusAndClearScheduledAtBefore(PaymentStatus.PENDING, now);

        operations.forEach(operation -> paymentService.forcedPayment(operation.getId()));
    }
}
