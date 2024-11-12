package faang.school.paymentservice.service;

import org.springframework.data.domain.Pageable;

public interface PendingService {

    boolean cleanBatch(Pageable pageable);
}
