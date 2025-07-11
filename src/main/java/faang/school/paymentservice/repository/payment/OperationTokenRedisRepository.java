package faang.school.paymentservice.repository.payment;

import faang.school.paymentservice.model.redis.payment.OperationTokenRedisModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OperationTokenRedisRepository extends CrudRepository<OperationTokenRedisModel, UUID> {
}
