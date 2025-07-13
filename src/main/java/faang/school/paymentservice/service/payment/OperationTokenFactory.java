package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.entity.payment.PaymentOperation;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class OperationTokenFactory {
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    public UUID buildRedisToken(PaymentOperation paymentOperation) {
        String payload = String.join(":",
                paymentOperation.getAccountFromId().toString(),
                paymentOperation.getAccountToId().toString(),
                paymentOperation.getCurrencyId().toString(),
                paymentOperation.getAmount().stripTrailingZeros().toPlainString()
        );
        return uuid(payload);
    }
    public UUID buildDbToken(PaymentOperation paymentOperation, LocalDateTime createdAt) {
        String payload = String.join(":",
                paymentOperation.getAccountFromId().toString(),
                paymentOperation.getAccountToId().toString(),
                paymentOperation.getCurrencyId().toString(),
                paymentOperation.getAmount().stripTrailingZeros().toPlainString(),
                createdAt.toString()
        );
        return uuid(payload);
    }

    private UUID uuid(String payload) {
        return UUID.nameUUIDFromBytes(payload.getBytes(UTF_8));
    }
}