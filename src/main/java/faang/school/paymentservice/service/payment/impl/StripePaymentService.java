package faang.school.paymentservice.service.payment.impl;

import faang.school.paymentservice.client.UserServiceClient;
import faang.school.paymentservice.dto.order.OrderDto;
import faang.school.paymentservice.dto.payment.PaymentRequest;
import faang.school.paymentservice.exception.PaymentSignatureInvalidException;
import faang.school.paymentservice.service.OrderService;
import faang.school.paymentservice.service.payment.PaymentService;
import org.springframework.stereotype.Service;

@Service("stripe")
public class StripePaymentService extends PaymentService {
    private final UserServiceClient userServiceClient;

    public StripePaymentService(OrderService orderService, UserServiceClient userServiceClient) {
        super(orderService);
        this.userServiceClient = userServiceClient;
    }

    @Override
    public String createPaymentLink(long orderId) {
        return "https://stripe.com/payment-link?orderId=" + orderId;
    }

    @Override
    protected void verifySignature(String secretKey) {
        if (!secretKey.equalsIgnoreCase("stripe-signature")) {
            throw new PaymentSignatureInvalidException("Секретный ключ не верный");
        }
    }

    @Override
    protected void notifyOtherServices(OrderDto orderDto, PaymentRequest paymentRequest) {
        userServiceClient.activatePremiumForUser(orderDto.getId());
    }
}
