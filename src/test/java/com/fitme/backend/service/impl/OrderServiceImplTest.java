package com.fitme.backend.service.impl;

import com.fitme.backend.dto.*;
import com.fitme.backend.entity.*;
import com.fitme.backend.repository.AppUserRepository;
import com.fitme.backend.repository.DishRepository;
import com.fitme.backend.repository.OrderRepository;
import com.fitme.backend.repository.PaymentAttemptRepository;
import com.fitme.backend.service.interfaces.PaymentGateway;
import com.fitme.backend.service.interfaces.PaymentGatewayResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class OrderServiceImplTest {

  @Mock
  private AppUserRepository appUserRepository;

  @Mock
  private DishRepository dishRepository;

  @Mock
  private OrderRepository orderRepository;

  @Mock
  private PaymentAttemptRepository paymentAttemptRepository;

  @Mock
  private PaymentGateway paymentGateway;

  @InjectMocks
  private OrderServiceImpl orderService;

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void createOrder_ShouldCreateOrderWithServerComputedTotal() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("john", "n/a"));

    AppUser user = AppUser.builder().id(1L).username("john").build();

    Restaurant restaurant = Restaurant.builder().id(5L).name("Fitme Kitchen").build();
    Dish dish = Dish.builder().id(10L).name("Bowl").price(12.5).restaurant(restaurant).build();

    when(appUserRepository.findByUsername("john")).thenReturn(Optional.of(user));
    when(orderRepository.findByAppUserIdAndIdempotencyKey(1L, "idem-1")).thenReturn(Optional.empty());
    when(dishRepository.findAllById(List.of(10L))).thenReturn(List.of(dish));
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
      Order order = invocation.getArgument(0);
      order.setId(99L);
      return order;
    });

    CreateOrderInput input = new CreateOrderInput(List.of(new CreateOrderItemInput(10L, 2)), "idem-1");

    CreateOrderResponseDto response = orderService.createOrder(input);

    assertNotNull(response.order());
    assertEquals(99L, response.order().id());
    assertEquals(OrderStatus.CREATED, response.order().status());
    assertEquals(25.0, response.order().totalAmount());
    assertEquals(1, response.order().items().size());

    ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
    verify(orderRepository, times(1)).save(orderCaptor.capture());
    assertEquals(25.0, orderCaptor.getValue().getTotalAmount());
  }

  @Test
  void confirmPayment_ShouldMarkOrderPaidWhenGatewayApproves() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("john", "n/a"));

    AppUser user = AppUser.builder().id(1L).username("john").build();
    Order order = Order.builder()
        .id(200L)
        .appUser(user)
        .status(OrderStatus.CREATED)
        .totalAmount(45.0)
        .idempotencyKey("create-idem")
        .build();

    when(appUserRepository.findByUsername("john")).thenReturn(Optional.of(user));
    when(orderRepository.findByAppUserIdAndId(1L, 200L)).thenReturn(Optional.of(order));
    when(paymentAttemptRepository.findByOrderIdAndIdempotencyKey(200L, "pay-idem")).thenReturn(Optional.empty());
    when(paymentGateway.confirmPayment(order, "MOCK-OK"))
        .thenReturn(new PaymentGatewayResult(true, "tx-1", "approved"));
    when(paymentGateway.providerName()).thenReturn("mock");
    when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
    when(paymentAttemptRepository.save(any(PaymentAttempt.class))).thenAnswer(invocation -> {
      PaymentAttempt attempt = invocation.getArgument(0);
      attempt.setId(300L);
      return attempt;
    });

    ConfirmPaymentResponseDto response = orderService.confirmPayment(
        new ConfirmPaymentInput(200L, "MOCK-OK", "pay-idem"));

    assertNotNull(response.order());
    assertEquals(OrderStatus.PAID, response.order().status());
    assertNotNull(response.paymentAttempt());
    assertEquals(PaymentStatus.SUCCEEDED, response.paymentAttempt().status());

    verify(orderRepository, times(1)).save(any(Order.class));
    verify(paymentAttemptRepository, times(1)).save(any(PaymentAttempt.class));
  }

  @Test
  void createOrder_ShouldReturnExistingOrderWhenIdempotencyKeyAlreadyUsed() {
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken("john", "n/a"));

    AppUser user = AppUser.builder().id(1L).username("john").build();
    Order existingOrder = Order.builder()
        .id(500L)
        .appUser(user)
        .status(OrderStatus.CREATED)
        .totalAmount(18.0)
        .idempotencyKey("idem-existing")
        .items(List.of())
        .build();

    when(appUserRepository.findByUsername("john")).thenReturn(Optional.of(user));
    when(orderRepository.findByAppUserIdAndIdempotencyKey(1L, "idem-existing"))
        .thenReturn(Optional.of(existingOrder));
    when(paymentAttemptRepository.findTopByOrderIdOrderByCreatedAtDesc(500L)).thenReturn(Optional.empty());

    CreateOrderResponseDto response = orderService.createOrder(
        new CreateOrderInput(List.of(new CreateOrderItemInput(10L, 1)), "idem-existing"));

    assertEquals(500L, response.order().id());
    assertEquals("Order already created for this idempotency key", response.message());
    verify(orderRepository, never()).save(any(Order.class));
  }
}
