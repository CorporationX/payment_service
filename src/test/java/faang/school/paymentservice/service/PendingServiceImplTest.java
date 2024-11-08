package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PendingDto;
import faang.school.paymentservice.entity.Pending;
import faang.school.paymentservice.entity.PendingStatus;
import faang.school.paymentservice.mapper.PendingMapper;
import faang.school.paymentservice.repository.PendingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PendingServiceImplTest {

    @Mock
    private PendingRepository pendingRepository;

    @Spy
    private PendingMapper pendingMapper;

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private PendingServiceImpl pendingService;

    private PageRequest pageRequest;

    @BeforeEach
    void setUp() {
        int batchSize = 10;
        pageRequest = PageRequest.of(0, batchSize);

        ReflectionTestUtils.setField(pendingService, "batchSize", batchSize);
        lenient().when(applicationContext.getBean(PendingServiceImpl.class)).thenReturn(pendingService);
    }

    @Test
    void cleaningPendings_withNoMorePendingRecords_stopsProcessing() {
        when(pendingRepository.findByStatus(PendingStatus.INITIALIZATION, pageRequest)).thenReturn(Page.empty());

        pendingService.cleaningPendings();

        verify(pendingRepository).findByStatus(PendingStatus.INITIALIZATION, pageRequest);
    }

    @Test
    void cleanBatch_withPendingRecords_updatesStatusAndPublishes() {
        Pending first = Pending.builder()
                .status(PendingStatus.INITIALIZATION)
                .build();
        Pending second = Pending.builder()
                .status(PendingStatus.INITIALIZATION)
                .build();
        List<Pending> pendings = List.of(first, second);
        Page<Pending> page = new PageImpl<>(pendings, pageRequest, pendings.size());

        when(pendingRepository.findByStatus(PendingStatus.INITIALIZATION, pageRequest)).thenReturn(page);

        boolean isEmpty = pendingService.cleanBatch(pageRequest);

        assertFalse(isEmpty);
        verify(pendingRepository).findByStatus(PendingStatus.INITIALIZATION, pageRequest);
        assertEquals(PendingStatus.IN_PROGRESS, first.getStatus());
        assertEquals(PendingStatus.IN_PROGRESS, second.getStatus());
    }

    @Test
    void cleanBatch_withEmptyPage_returnsTrue() {
        when(pendingRepository.findByStatus(PendingStatus.INITIALIZATION, pageRequest)).thenReturn(Page.empty());

        boolean isEmpty = pendingService.cleanBatch(pageRequest);

        assertTrue(isEmpty);
    }
}
