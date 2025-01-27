package faang.school.paymentservice.service;

import faang.school.paymentservice.client.UserServiceClient;
import faang.school.paymentservice.dto.BooleanResponse;
import faang.school.paymentservice.dto.order.CreateOrderDto;
import faang.school.paymentservice.dto.order.OrderDto;
import faang.school.paymentservice.dto.order.ServiceType;
import faang.school.paymentservice.dto.payment.PaymentStatus;
import faang.school.paymentservice.entity.Order;
import faang.school.paymentservice.entity.ServicePlan;
import faang.school.paymentservice.exception.BusinessException;
import faang.school.paymentservice.exception.EntityNotFoundException;
import faang.school.paymentservice.mapper.OrderMapper;
import faang.school.paymentservice.repository.OrderRepository;
import faang.school.paymentservice.repository.ServicePlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ServicePlanRepository servicePlanRepository;
    @Mock
    private OrderMapper orderMapper;
    @InjectMocks
    private OrderService orderService;

    private Order order;
    private ServicePlan servicePlan;
    CreateOrderDto createOrderDto;

    @BeforeEach
    void setUp() {
        servicePlan = ServicePlan.builder()
                .name("basic")
                .serviceType(ServiceType.PREMIUM)
                .cost(100)
                .build();

        order = Order.builder()
                .servicePlan(servicePlan)
                .paymentMethod("crypto")
                .paymentStatus(PaymentStatus.PENDING)
                .userId(1L)
                .cost(servicePlan.getCost())
                .build();

        createOrderDto = new CreateOrderDto(
                ServiceType.PREMIUM,
                "basic",
                "crypto",
                1L
        );
    }

    @Test
    void testCreateOrderSuccess() {
        mockOnSuccessServicePlanRepository();
        mockOnSuccessUserServiceClient();
        when(orderRepository.save(any(Order.class)))
                .thenReturn(order);

        OrderDto orderDto = orderService.createOrder(createOrderDto);

        verify(orderRepository, times(1)).save(order);
        assertEquals(orderMapper.toDto(order), orderDto);
    }

    @Test
    void testCreateOrderWithInvalidServicePlan() {
        when(servicePlanRepository.getPlanByName("basic"))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createOrderDto));
    }

    @Test
    void testCreateOrderWithInvalidServiceType() {
        mockOnSuccessServicePlanRepository();

        createOrderDto.setServiceType(ServiceType.PROMOTION);

        assertThrows(BusinessException.class, () -> orderService.createOrder(createOrderDto));
    }

    @Test
    void testCreateOrderUserNotFound() {
        mockOnSuccessServicePlanRepository();
        when(userServiceClient.isUserExist(1L))
                .thenReturn(new BooleanResponse(false));

        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(createOrderDto));
    }

    @Test
    void testUpdateOrderStatusToSuccess() {
        ArgumentCaptor<Order> argumentCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        orderService.updateOrderStatusToSuccess(1L);

        verify(orderRepository, atLeastOnce()).save(argumentCaptor.capture());
        assertEquals(PaymentStatus.SUCCESS, argumentCaptor.getValue().getPaymentStatus());
    }

    @Test
    void testUpdateOrderStatusToSuccessOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.updateOrderStatusToSuccess(1L));
    }

    @Test
    void testGetOrderSuccess() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        OrderDto orderDto = orderService.getOrder(1L);

        assertEquals(orderMapper.toDto(order), orderDto);
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> orderService.getOrder(1L));
    }

    private void mockOnSuccessServicePlanRepository() {
        when(servicePlanRepository.getPlanByName("basic"))
                .thenReturn(Optional.of(servicePlan));
    }

    private void mockOnSuccessUserServiceClient() {
        when(userServiceClient.isUserExist(1L))
                .thenReturn(new BooleanResponse(true));
    }
}