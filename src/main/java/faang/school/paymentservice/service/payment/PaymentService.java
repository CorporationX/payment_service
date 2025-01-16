package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.order.OrderDto;
import faang.school.paymentservice.dto.payment.PaymentRequest;
import faang.school.paymentservice.service.OrderService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class PaymentService {
    protected final OrderService orderService;

    public abstract String createPaymentLink(long orderId);
    protected abstract void verifySignature(String secretKey);
    protected abstract void notifyOtherServices(OrderDto orderDto, PaymentRequest paymentRequest);

    public void processPayment(PaymentRequest paymentRequest, String secretKey) {
        verifySignature(secretKey);
        OrderDto orderDto = orderService.updateOrderStatusToSuccess(paymentRequest.getOrderId());
        notifyOtherServices(orderDto, paymentRequest);
    }
}
