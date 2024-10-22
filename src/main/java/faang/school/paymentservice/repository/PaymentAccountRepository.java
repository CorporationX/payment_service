package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.PaymentAccount;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentAccountRepository extends CrudRepository<PaymentAccount, Long> {
}
