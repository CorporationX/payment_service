package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.account.Account;
import faang.school.paymentservice.model.owner.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    @Query("""
        SELECT a FROM Account a
        WHERE a.owner.externalId = :externalId
        AND a.owner.type = :type
        """)
    List<Account> findByOwner(Long externalId, OwnerType type);
}
