package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.order.CreateOrderDto;
import faang.school.paymentservice.dto.order.OrderDto;
import faang.school.paymentservice.exception.DataValidationException;
import faang.school.paymentservice.service.OrderService;
import faang.school.paymentservice.service.payment.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final Map<String, PaymentService> paymentServicesMap;

    @PostMapping
    public OrderDto createOrder(@RequestBody @Valid CreateOrderDto dto) {
        PaymentService paymentService = getPaymentMethod(dto.paymentMethod());
        OrderDto orderDto = orderService.createOrder(dto);
        String paymentLink = paymentService.createPaymentLink(orderDto.getId());
        orderDto.setPaymentLink(paymentLink);
        return orderDto;
    }

    @GetMapping("{orderId}")
    public OrderDto getOrder(@PathVariable Long orderId) {
        return orderService.getOrder(orderId);
    }

    private PaymentService getPaymentMethod(String paymentMethod) {
        PaymentService paymentService = paymentServicesMap.get(paymentMethod);
        if (paymentService == null) {
            throw new DataValidationException("Такой способ оплаты не предусмотрен");
        }
        return paymentService;
    }
}
