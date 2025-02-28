package faang.school.paymentservice.repository;

import faang.school.paymentservice.dto.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Stream<Account> findByUserId(long userId);
    Stream<Account> findByProjectId(long projectId);
}
