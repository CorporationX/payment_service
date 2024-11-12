package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.PendingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClearingSchedulerTest {

    @Mock
    private PendingService pendingService;

    @InjectMocks
    private ClearingScheduler clearingScheduler;

    private int batchSize;

    @BeforeEach
    void setUp() {
        batchSize = 10;
        ReflectionTestUtils.setField(clearingScheduler, "batchSize", batchSize);
    }

    @Test
    void clearing_whenBatchesExist_processesAllPages() {
        when(pendingService.cleanBatch(any(PageRequest.class)))
                .thenReturn(false)
                .thenReturn(false)
                .thenReturn(true);

        clearingScheduler.clearing();

        verify(pendingService, times(3)).cleanBatch(any(PageRequest.class));
        verify(pendingService).cleanBatch(PageRequest.of(0, batchSize));
        verify(pendingService).cleanBatch(PageRequest.of(1, batchSize));
        verify(pendingService).cleanBatch(PageRequest.of(2, batchSize));
    }

    @Test
    void clearing_whenNoBatchesExist_stopsAfterFirstPage() {
        when(pendingService.cleanBatch(any(PageRequest.class))).thenReturn(true);

        clearingScheduler.clearing();

        verify(pendingService).cleanBatch(PageRequest.of(0, batchSize));
    }
}