package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PendingDto;
import faang.school.paymentservice.entity.Pending;
import faang.school.paymentservice.entity.PendingStatus;
import faang.school.paymentservice.mapper.PendingMapper;
import faang.school.paymentservice.repository.PendingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PendingServiceImpl implements PendingService {

    private final PendingRepository pendingRepository;
    private final PendingMapper pendingMapper;

    @Transactional
    public boolean cleanBatch(Pageable pageable) {
        Page<Pending> page = pendingRepository.findByStatus(PendingStatus.INITIALIZATION, pageable);
        List<PendingDto> pendingDtos = page.stream()
                .peek(pending -> pending.setStatus(PendingStatus.IN_PROGRESS))
                .map(pendingMapper::toDto)
                .toList();
        // publish
        return page.isEmpty();
    }
}
