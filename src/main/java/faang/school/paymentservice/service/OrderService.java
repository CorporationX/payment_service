package faang.school.paymentservice.service;

import faang.school.paymentservice.client.UserServiceClient;
import faang.school.paymentservice.dto.order.CreateOrderDto;
import faang.school.paymentservice.dto.order.OrderDto;
import faang.school.paymentservice.dto.payment.PaymentStatus;
import faang.school.paymentservice.entity.Order;
import faang.school.paymentservice.entity.ServicePlan;
import faang.school.paymentservice.exception.BusinessException;
import faang.school.paymentservice.exception.EntityNotFoundException;
import faang.school.paymentservice.mapper.OrderMapper;
import faang.school.paymentservice.repository.OrderRepository;
import faang.school.paymentservice.repository.ServicePlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final UserServiceClient userServiceClient;
    private final OrderRepository orderRepository;
    private final ServicePlanRepository servicePlanRepository;
    private final OrderMapper orderMapper;

    public OrderDto createOrder(CreateOrderDto dto, long userId) {
        ServicePlan servicePlan = getServicePlan(dto.plan(), dto.serviceType());

        if (!userServiceClient.isUserExist(userId).success()) {
            throw new EntityNotFoundException("Пользователь не найден");
        }

        Order order = Order.builder()
                .servicePlan(servicePlan)
                .paymentMethod(dto.paymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .userId(userId)
                .cost(servicePlan.getCost())
                .build();
        order = orderRepository.save(order);

        log.info("Заказ с id {} был создан", order.getId());
        return orderMapper.toDto(order);
    }

    public OrderDto updateOrderStatusToSuccess(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Заказ с id " + orderId + " не найден"));
        order.setPaymentStatus(PaymentStatus.SUCCESS);
        return orderMapper.toDto(orderRepository.save(order));
    }

    public OrderDto getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Заказ с id " + orderId + " не найден"));
        return orderMapper.toDto(order);
    }

    private ServicePlan getServicePlan(String planName, String serviceTypeName) {
        ServicePlan servicePlan = servicePlanRepository.getPlanByName(planName.toLowerCase())
                .orElseThrow(() -> new EntityNotFoundException("Тарифный план не найден"));

        if (!servicePlan.getServiceType().getName().equalsIgnoreCase(serviceTypeName)) {
            throw new BusinessException("Тип услуги не соответствует тарифному плану");
        }

        return servicePlan;
    }
}
