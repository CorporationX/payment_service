package faang.school.paymentservice.service.payment.impl;

import faang.school.paymentservice.client.UserServiceClient;
import faang.school.paymentservice.dto.order.OrderDto;
import faang.school.paymentservice.dto.payment.PaymentRequest;
import faang.school.paymentservice.exception.PaymentSignatureInvalidException;
import faang.school.paymentservice.service.OrderService;
import faang.school.paymentservice.service.payment.PaymentService;
import org.springframework.stereotype.Service;

@Service("crypto")
public class CryptoPaymentService extends PaymentService {
    private final UserServiceClient userServiceClient;

    public CryptoPaymentService(OrderService orderService, UserServiceClient userServiceClient) {
        super(orderService);
        this.userServiceClient = userServiceClient;
    }

    @Override
    public String createPaymentLink(long orderId) {
        return "https://crypto.com/payment-link?orderId=" + orderId;
    }

    @Override
    protected void verifySignature(String secretKey) {
        if (!secretKey.equalsIgnoreCase("crypto-signature")) {
            throw new PaymentSignatureInvalidException("Секретный ключ не верный");
        }
    }

    @Override
    protected void notifyOtherServices(OrderDto orderDto, PaymentRequest paymentRequest) {
        userServiceClient.activatePremiumForUser(orderDto.getId());
    }
}
