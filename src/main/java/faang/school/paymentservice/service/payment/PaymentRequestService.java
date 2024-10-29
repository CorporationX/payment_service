package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.event.dmsevent.DmsTypeOperation;
import faang.school.paymentservice.dto.request.RequestDto;
import faang.school.paymentservice.dto.event.dmsevent.DmsEventDto;
import faang.school.paymentservice.entity.request.Request;
import faang.school.paymentservice.entity.request.RequestStatus;
import faang.school.paymentservice.mapper.EventMapper;
import faang.school.paymentservice.mapper.RequestMapper;
import faang.school.paymentservice.publisher.AbstractEventPublisher;
import faang.school.paymentservice.repository.RequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public RequestDto cancelPayment(long requestId) {
        Request request = findRequest(requestId);
        if (request.getStatus() != RequestStatus.PENDING) {
            String message = "The payment request with id %d has already been cancelled or completed"
                .formatted(requestId);
            log.error(message);
            throw new RuntimeException(message);
        }
        publishMessage(request, DmsTypeOperation.CANCELING);
        request.setStatus(RequestStatus.CANCELING);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    public RequestDto forciblyConfirmPayment(long requestId) {
        Request request = findRequest(requestId);
        if (request.getStatus() != RequestStatus.PENDING) {
            String message = "The payment request with id %d has already been completed or cancelled"
                .formatted(requestId);
            log.error(message);
            throw new RuntimeException(message);
        }
        publishMessage(request, DmsTypeOperation.CONFIRMATION);
        request.setStatus(RequestStatus.COMPLETED);
        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    public void pushPaymentConfirmation() {
        List<Request> requests = requestRepository.findToPushing();
        requests.forEach(
            request -> {
                try {
                    publishMessage(request, DmsTypeOperation.CONFIRMATION);
                    request.setStatus(RequestStatus.COMPLETED);
                    requestRepository.save(request);
                } catch (RuntimeException e) {
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
        Optional<Request> requestOpt = requestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            String message = "Request with id = %d not found".formatted(requestId);
            log.error(message);
            throw new RuntimeException(message);
        }
        return  requestOpt.get();
    }
}
