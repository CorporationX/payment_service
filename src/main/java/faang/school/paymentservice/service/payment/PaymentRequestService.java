package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.event.dmsevent.DmsTypeOperation;
import faang.school.paymentservice.dto.request.RequestDto;
import faang.school.paymentservice.dto.event.dmsevent.DmsEventDto;
import faang.school.paymentservice.entity.request.Request;
import faang.school.paymentservice.entity.request.RequestStatus;
import faang.school.paymentservice.exception.PaymentRequestException;
import faang.school.paymentservice.mapper.EventMapper;
import faang.school.paymentservice.mapper.RequestMapper;
import faang.school.paymentservice.publisher.AbstractEventPublisher;
import faang.school.paymentservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestService {
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final EventMapper eventMapper;
    private final AbstractEventPublisher<DmsEventDto> dmsEventPublisher;

    public RequestDto getRequest(long requestId) {
        Request request = findRequest(requestId);
        return requestMapper.toRequestDto(request);
    }

    public RequestDto authorizePayment(RequestDto requestDto) {
        Request request = requestMapper.toRequest(requestDto);
        request.setStatus(RequestStatus.PENDING);
        Request savedRequest = requestRepository.save(request);
        publishMessage(savedRequest, DmsTypeOperation.AUTHORIZATION);
        return requestMapper.toRequestDto(savedRequest);
    }

    @Transactional
    public RequestDto cancelPayment(long requestId) {
        return processPayment(requestId, RequestStatus.CANCELING, DmsTypeOperation.CANCELING);
    }

    public RequestDto forciblyConfirmPayment(long requestId) {
        return processPayment(requestId, RequestStatus.COMPLETED, DmsTypeOperation.CONFIRMATION);
    }

    @Transactional
    public void pushPaymentConfirmation() {
        List<Request> requests = requestRepository.findToPushing(RequestStatus.PENDING.name());
        requests.forEach(
            request -> {
                try {
                    request.setStatus(RequestStatus.COMPLETED);
                    requestRepository.save(request);
                    publishMessage(request, DmsTypeOperation.CONFIRMATION);
                } catch (PaymentRequestException e) {
                    log.warn("Confirmation for the request with id {} could not be sent", request.getId(), e);
                }
            }
        );
    }

    private void publishMessage(Request request, DmsTypeOperation typeOperation) {
        DmsEventDto dmsEventDto = eventMapper.requestToDmsEventDto(request);
        dmsEventDto.setTypeOperation(typeOperation);
        dmsEventPublisher.publish(dmsEventDto);
    }

    private Request findRequest(long requestId) {
        return requestRepository.findById(requestId).orElseThrow(() -> {
            String message = "Request with id = %d not found".formatted(requestId);
            return new PaymentRequestException(message);
        });
    }

    private void checkRequestAbleToCancelOrConfirm(Request request) {
        if (request.getStatus() != RequestStatus.PENDING) {
            String message = "The payment request with id %d has already been cancelled or completed"
                .formatted(request.getId());
            throw new PaymentRequestException(message);
        }
    }

    private RequestDto processPayment(long requestId, RequestStatus status, DmsTypeOperation typeOperation) {
        Request request = findRequest(requestId);
        checkRequestAbleToCancelOrConfirm(request);
        request.setStatus(status);
        Request returnedRequest = requestRepository.save(request);
        RequestDto requestDto = requestMapper.toRequestDto(returnedRequest);
        publishMessage(returnedRequest, typeOperation);
        return requestDto;
    }
}
