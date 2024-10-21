package faang.school.paymentservice.repository;

import faang.school.paymentservice.model.owner.Owner;
import faang.school.paymentservice.model.owner.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OwnerRepository extends JpaRepository<Owner, Long> {

    //    @Query(nativeQuery = true, value = """
//            SELECT COUNT(*) > 1 FROM payment_owner
//            WHERE external_id = :externalId AND type = :type
//            """)
//    boolean isOwnerExist(Long externalId, OwnerType type);
//
    @Query("""
            SELECT o FROM Owner o
            WHERE o.externalId = :externalId AND o.type = :type
            """)
    Optional<Owner> findOwner(Long externalId, OwnerType type);

}
