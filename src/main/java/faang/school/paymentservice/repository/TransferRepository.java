package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {
}
