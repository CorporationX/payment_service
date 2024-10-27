package faang.school.paymentservice.aop.aspects;

import faang.school.paymentservice.annotations.SendPendingOperationMessage;
import faang.school.paymentservice.model.OperationType;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.service.OperationMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class PendingOperationMessageAspect {
    private final OperationMessageService operationMessageService;

    @AfterReturning(pointcut = "@annotation(sendPendingOperationMessage)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, SendPendingOperationMessage sendPendingOperationMessage, Object result) {
        OperationType operationType = sendPendingOperationMessage.value();
        UUID operationId = null;

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof UUID) {
                operationId = (UUID) arg;
                break;
            } else if (arg instanceof PendingOperation) {
                operationId = ((PendingOperation) arg).getId();
                break;
            }
        }

        if (operationId == null && result instanceof UUID) {
            operationId = (UUID) result;
        } else if (operationId == null && result instanceof PendingOperation) {
            operationId = ((PendingOperation) result).getId();
        }

        if (operationId == null) {
            log.error("Failed to find operationId in method arguments or return value");
            throw new IllegalArgumentException("Failed to find operationId in method arguments or return value");
        }

        log.info("Publishing message with operation ID: {} and operation type: {}", operationId, operationType);
        operationMessageService.sendOperationMessage(operationId, operationType);
    }
}