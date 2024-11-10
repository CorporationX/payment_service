package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.request.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query(nativeQuery = true, value = """
        select * from request
        where now() >= clear_scheduled_at
            and status = ?1
        """
    )
    List<Request> findToPushing(String status);
}
