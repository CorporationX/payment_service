package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.ServicePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ServicePlanRepository extends JpaRepository<ServicePlan, Long> {
    @Query(nativeQuery = true, value = """
            SELECT * FROM service_plans
            WHERE service_plans.name = ?1
            """)
    Optional<ServicePlan> getPlanByName(String name);
}
