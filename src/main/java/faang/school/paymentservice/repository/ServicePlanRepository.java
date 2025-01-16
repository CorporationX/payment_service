package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.ServicePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ServicePlanRepository extends JpaRepository<ServicePlan, Long> {
    @Query(nativeQuery = true, value = """
            SELECT sp.* FROM service_plans sp
            JOIN service_types st ON sp.service_type_id = st.id
            WHERE sp.name = ?1
            """)
    Optional<ServicePlan> getPlanByName(String name);
}
