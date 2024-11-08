package faang.school.paymentservice.service;


import faang.school.paymentservice.client.WebClientForAccountService;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PendingDto;
import faang.school.paymentservice.entity.Pending;
import faang.school.paymentservice.entity.PendingStatus;
import faang.school.paymentservice.mapper.PendingDtoMapper;
import faang.school.paymentservice.repository.PendingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PendingServiceTest {

    @Spy
    private PendingDtoMapper mapper;

    @Mock
    private PendingRepository pendingRepository;

    @Mock
    private WebClientForAccountService webClient;

    @InjectMocks
    private PendingServiceImpl pendingService;

    private PendingDto pendingDto;
    private Pending pending;

    @BeforeEach
    public void setUp() {
        pendingDto = new PendingDto();
        pendingDto.setId(1L);
        pendingDto.setAmount(new BigDecimal("1500.75"));
        pendingDto.setCurrency(Currency.USD);
        pendingDto.setFromAccountId(9876543210L);
        pendingDto.setToAccountId(1234567890L);

        pending = new Pending();
        pending.setId(1L);
        pending.setAmount(new BigDecimal("1500.75"));
        pending.setCurrency(Currency.USD);
        pending.setFromAccountId(9876543210L);
        pending.setToAccountId(1234567890L);
    }

    @Test
    void initAndSaving_shouldReturnExistingPendingId_whenPendingWithTokenExists() {
        UUID uuid = UUID.randomUUID();
        Long existingId = 1L;
        pending.setId(existingId);
        when(pendingRepository.findByToken(uuid)).thenReturn(Optional.of(pending));

        Long resultId = pendingService.initAndSaving(pendingDto, uuid);

        assertEquals(existingId, resultId);
        verify(pendingRepository, never()).save(any(Pending.class));
    }

    @Test
    void initAndSaving_shouldSaveAndReturnNewPendingId_whenPendingWithTokenDoesNotExist() {
        UUID uuid = UUID.randomUUID();
        pending.setId(2L);
        when(pendingRepository.findByToken(uuid)).thenReturn(Optional.empty());
        when(mapper.toEntity(pendingDto)).thenReturn(pending);
        when(pendingRepository.save(any(Pending.class))).thenReturn(pending);

        Long resultId = pendingService.initAndSaving(pendingDto, uuid);

        assertEquals(2L, resultId);
        verify(mapper).toEntity(pendingDto);
        verify(pendingRepository).save(pending);
        assertEquals(PendingStatus.INITIALIZATION, pending.getStatus());
        assertEquals(uuid, pending.getToken());
    }

    @Test
    void cancelPending_shouldUpdateStatusToCanceled_whenTokenMatches() {
        Long pendingId = 1L;
        UUID token = UUID.randomUUID();

        pending.setToken(token);
        pending.setStatus(PendingStatus.INITIALIZATION);

        when(pendingRepository.findById(pendingId)).thenReturn(Optional.of(pending));
        when(mapper.toDto(pending)).thenReturn(pendingDto);

        PendingDto result = pendingService.cancelPending(pendingId, token);

        assertEquals(PendingStatus.CANCELED, pending.getStatus());
        assertEquals(pendingDto, result);
        verify(webClient, never()).sendRequestCancelPending(any(PendingDto.class), anyString());
    }

    @Test
    void cancelPending_shouldCallWebClient_whenTokenDoesNotMatch() {
        Long pendingId = 1L;
        UUID token = UUID.randomUUID();
        UUID differentToken = UUID.randomUUID();

        pending.setToken(differentToken);
        pending.setStatus(PendingStatus.INITIALIZATION);

        when(pendingRepository.findById(pendingId)).thenReturn(Optional.of(pending));
        when(mapper.toDto(pending)).thenReturn(pendingDto);

        PendingDto result = pendingService.cancelPending(pendingId, token);

        verify(webClient).sendRequestCancelPending(pendingDto, "/api/v1/payment/pending/cancel/" + pendingId);
        assertEquals(pendingDto, result);
    }

    @Test
    void cancelPending_shouldThrowEntityNotFoundException_whenPendingNotFound() {
        Long pendingId = 1L;
        UUID token = UUID.randomUUID();

        when(pendingRepository.findById(pendingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pendingService.cancelPending(pendingId, token));
    }

    @Test
    void forcedPaymentConfirmation_shouldUpdateStatusToSuccess_whenTokenMatches() {
        Long pendingId = 1L;
        UUID token = UUID.randomUUID();

        pending.setToken(token);
        pending.setStatus(PendingStatus.INITIALIZATION);

        when(pendingRepository.findById(pendingId)).thenReturn(Optional.of(pending));
        when(mapper.toDto(pending)).thenReturn(pendingDto);

        PendingDto result = pendingService.forcedPaymentConfirmation(pendingId, token);

        assertEquals(PendingStatus.SUCCESS, pending.getStatus());
        assertEquals(pendingDto, result);
        verify(webClient, never()).sendRequestCancelPending(any(PendingDto.class), anyString());
    }

    @Test
    void forcedPaymentConfirmation_shouldCallWebClient_whenTokenDoesNotMatch() {
        Long pendingId = 1L;
        UUID token = UUID.randomUUID();
        UUID differentToken = UUID.randomUUID();

        pending.setToken(differentToken);
        pending.setStatus(PendingStatus.INITIALIZATION);

        when(pendingRepository.findById(pendingId)).thenReturn(Optional.of(pending));
        when(mapper.toDto(pending)).thenReturn(pendingDto);

        PendingDto result = pendingService.forcedPaymentConfirmation(pendingId, token);

        verify(webClient).sendRequestCancelPending(pendingDto, "/api/v1/payment/clearing/" + pendingId);
        assertEquals(pendingDto, result);
    }

    @Test
    void forcedPaymentConfirmation_shouldThrowEntityNotFoundException_whenPendingNotFound() {
        Long pendingId = 1L;
        UUID token = UUID.randomUUID();

        when(pendingRepository.findById(pendingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> pendingService.forcedPaymentConfirmation(pendingId, token));
    }

    @Test
    void getPending_shouldReturnPendingDto_whenPendingExists() {
        Long pendingId = 1L;

        when(pendingRepository.findById(pendingId)).thenReturn(Optional.of(pending));
        when(mapper.toDto(pending)).thenReturn(pendingDto);

        PendingDto result = pendingService.getPending(pendingId);

        assertEquals(pendingDto, result);
        verify(pendingRepository).findById(pendingId);
        verify(mapper).toDto(pending);
    }

    @Test
    void getPending_shouldThrowEntityNotFoundException_whenPendingNotFound() {
        Long pendingId = 1L;

        when(pendingRepository.findById(pendingId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> pendingService.getPending(pendingId));

        assertEquals("No pending found with 1", exception.getMessage());
        verify(pendingRepository).findById(pendingId);
        verify(mapper, never()).toDto(any(Pending.class));
    }

    @Test
    void resetStatus_shouldUpdatePendingStatus_whenPendingExists() {
        pendingDto.setStatus(PendingStatus.CANCELED);
        pending.setStatus(PendingStatus.INITIALIZATION);

        when(pendingRepository.findById(pendingDto.getId())).thenReturn(Optional.of(pending));

        pendingService.resetStatus(pendingDto);

        assertEquals(PendingStatus.CANCELED, pending.getStatus());
        verify(pendingRepository).findById(pendingDto.getId());
        verify(pendingRepository, never()).save(any(Pending.class));
    }
}
