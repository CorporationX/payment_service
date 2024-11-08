package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PendingDto;
import faang.school.paymentservice.entity.Pending;
import faang.school.paymentservice.entity.PendingStatus;
import faang.school.paymentservice.mapper.PendingMapper;
import faang.school.paymentservice.repository.PendingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PendingServiceImpl implements PendingService {

    private final PendingRepository pendingRepository;
    private final PendingMapper pendingMapper;
    private final ApplicationContext applicationContext;

    @Value("${clearing.batch-size}")
    private int batchSize;

    @Override
    public void cleaningPendings() {
        PendingServiceImpl self = applicationContext.getBean(PendingServiceImpl.class);
        int pageNumber = 0;
        boolean isEmpty = false;

        while (!isEmpty) {
            PageRequest pageable = PageRequest.of(pageNumber, batchSize);
            isEmpty = self.cleanBatch(pageable);
            pageNumber++;
        }
    }

    @Transactional
    public boolean cleanBatch(Pageable pageable) {
        Page<Pending> page = pendingRepository.findByStatus(PendingStatus.INITIALIZATION, pageable);
        List<Pending> pendings = page.stream()
                .peek(pending -> pending.setStatus(PendingStatus.IN_PROGRESS))
                .toList();
        List<PendingDto> pendingDtos = pendingMapper.toDto(pendings);
        // publish
        return page.isEmpty();
    }
}
