package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.BalanceAudit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface BalanceAuditRepository extends CrudRepository<BalanceAudit, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM balance_audit WHERE status = 0 ORDER BY created ASC LIMIT :amount")
    public List<BalanceAudit> findSomeRequests(Long amount);
}
