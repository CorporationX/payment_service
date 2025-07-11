package faang.school.paymentservice.model.payment;

import faang.school.paymentservice.model.redis.payment.OperationTokenRedisModel;

public record OperationTokenResult(
        OperationTokenRedisModel tokenModel,
        boolean wasAlreadyPresent
) {
}
