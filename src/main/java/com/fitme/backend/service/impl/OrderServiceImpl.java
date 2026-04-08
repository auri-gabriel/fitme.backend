package com.fitme.backend.service.impl;

import com.fitme.backend.dto.*;
import com.fitme.backend.entity.*;
import com.fitme.backend.mappers.OrderMapper;
import com.fitme.backend.mappers.PaymentAttemptMapper;
import com.fitme.backend.repository.AppUserRepository;
import com.fitme.backend.repository.DishRepository;
import com.fitme.backend.repository.OrderRepository;
import com.fitme.backend.repository.PaymentAttemptRepository;
import com.fitme.backend.service.interfaces.OrderService;
import com.fitme.backend.service.interfaces.PaymentGateway;
import com.fitme.backend.service.interfaces.PaymentGatewayResult;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

  private final AppUserRepository appUserRepository;
  private final DishRepository dishRepository;
  private final OrderRepository orderRepository;
  private final PaymentAttemptRepository paymentAttemptRepository;
  private final PaymentGateway paymentGateway;
  private final OrderMapper orderMapper;
  private final PaymentAttemptMapper paymentAttemptMapper;

  public OrderServiceImpl(
      AppUserRepository appUserRepository,
      DishRepository dishRepository,
      OrderRepository orderRepository,
      PaymentAttemptRepository paymentAttemptRepository,
      PaymentGateway paymentGateway,
      OrderMapper orderMapper,
      PaymentAttemptMapper paymentAttemptMapper) {
    this.appUserRepository = appUserRepository;
    this.dishRepository = dishRepository;
    this.orderRepository = orderRepository;
    this.paymentAttemptRepository = paymentAttemptRepository;
    this.paymentGateway = paymentGateway;
    this.orderMapper = orderMapper;
    this.paymentAttemptMapper = paymentAttemptMapper;
  }

  @Override
  @Transactional
  public CreateOrderResponseDto createOrder(CreateOrderInput input) {
    if (input == null) {
      throw new IllegalArgumentException("Input is required");
    }
    if (input.idempotencyKey() == null || input.idempotencyKey().isBlank()) {
      throw new IllegalArgumentException("Idempotency key is required");
    }
    if (input.items() == null || input.items().isEmpty()) {
      throw new IllegalArgumentException("Order must have at least one item");
    }

    AppUser currentUser = getCurrentUser();

    Optional<Order> existing = orderRepository.findByAppUserIdAndIdempotencyKey(
        currentUser.getId(), input.idempotencyKey());
    if (existing.isPresent()) {
      Order existingOrder = existing.get();
      PaymentAttempt existingAttempt = paymentAttemptRepository
          .findTopByOrderIdOrderByCreatedAtDesc(existingOrder.getId())
          .orElse(null);
      return new CreateOrderResponseDto(orderMapper.toDto(existingOrder), paymentAttemptMapper.toDto(existingAttempt),
          "Order already created for this idempotency key");
    }

    Map<Long, Integer> quantitiesByDishId = aggregateQuantities(input.items());
    List<Long> dishIds = new ArrayList<>(quantitiesByDishId.keySet());
    List<Dish> dishes = dishRepository.findAllById(dishIds);

    if (dishes.size() != dishIds.size()) {
      throw new IllegalArgumentException("One or more dishes were not found");
    }

    Map<Long, Dish> dishById = dishes.stream().collect(Collectors.toMap(Dish::getId, Function.identity()));

    double totalAmount = 0D;
    List<OrderItem> orderItems = new ArrayList<>();

    Order order = Order.builder()
        .appUser(currentUser)
        .status(OrderStatus.CREATED)
        .totalAmount(0D)
        .idempotencyKey(input.idempotencyKey())
        .build();

    for (Map.Entry<Long, Integer> entry : quantitiesByDishId.entrySet()) {
      Long dishId = entry.getKey();
      Integer quantity = entry.getValue();
      Dish dish = dishById.get(dishId);

      double lineTotal = dish.getPrice() * quantity;
      totalAmount += lineTotal;

      orderItems.add(OrderItem.builder()
          .order(order)
          .dishId(dish.getId())
          .dishName(dish.getName())
          .unitPrice(dish.getPrice())
          .quantity(quantity)
          .restaurantId(dish.getRestaurant().getId())
          .restaurantName(dish.getRestaurant().getName())
          .build());
    }

    order.setTotalAmount(totalAmount);
    order.setItems(orderItems);

    Order saved = orderRepository.save(order);
    return new CreateOrderResponseDto(orderMapper.toDto(saved), null, "Order created. Awaiting payment confirmation");
  }

  @Override
  @Transactional
  @SuppressWarnings("null")
  public ConfirmPaymentResponseDto confirmPayment(ConfirmPaymentInput input) {
    if (input == null) {
      throw new IllegalArgumentException("Input is required");
    }
    if (input.idempotencyKey() == null || input.idempotencyKey().isBlank()) {
      throw new IllegalArgumentException("Idempotency key is required");
    }
    if (input.orderId() == null) {
      throw new IllegalArgumentException("Order id is required");
    }

    AppUser currentUser = getCurrentUser();

    Order order = orderRepository.findByAppUserIdAndId(currentUser.getId(), input.orderId())
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    Optional<PaymentAttempt> previousAttempt = paymentAttemptRepository.findByOrderIdAndIdempotencyKey(
        order.getId(), input.idempotencyKey());

    if (previousAttempt.isPresent()) {
      PaymentAttempt attempt = previousAttempt.get();
      return new ConfirmPaymentResponseDto(
          orderMapper.toDto(order),
          paymentAttemptMapper.toDto(attempt),
          "Payment already confirmed for this idempotency key");
    }

    if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.CANCELLED) {
      throw new IllegalStateException("Order is in a final state and cannot be paid");
    }

    order.setStatus(OrderStatus.PAYMENT_PENDING);
    order.setPaymentReference(input.paymentReference());

    PaymentGatewayResult gatewayResult = paymentGateway.confirmPayment(order, input.paymentReference());

    PaymentStatus paymentStatus = gatewayResult.success() ? PaymentStatus.SUCCEEDED : PaymentStatus.FAILED;
    OrderStatus nextOrderStatus = gatewayResult.success() ? OrderStatus.PAID : OrderStatus.FAILED;

    order.setStatus(nextOrderStatus);
    Order savedOrder = orderRepository.save(order);

    PaymentAttempt attempt = PaymentAttempt.builder()
        .order(savedOrder)
        .status(paymentStatus)
        .idempotencyKey(input.idempotencyKey())
        .provider(paymentGateway.providerName())
        .providerTransactionId(gatewayResult.providerTransactionId())
        .paymentReference(input.paymentReference())
        .message(gatewayResult.message())
        .build();

    return new ConfirmPaymentResponseDto(
        orderMapper.toDto(savedOrder),
        paymentAttemptMapper.toDto(paymentAttemptRepository.save(attempt)),
        gatewayResult.message());
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderDto> getMyOrders() {
    AppUser currentUser = getCurrentUser();
    List<Order> orders = orderRepository.findByAppUserIdOrderByCreatedAtDesc(currentUser.getId());
    return orders.stream().map(order -> orderMapper.toDto(order)).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public OrderDto getMyOrder(Long id) {
    if (id == null) {
      throw new IllegalArgumentException("Order id is required");
    }

    AppUser currentUser = getCurrentUser();
    Order order = orderRepository.findByAppUserIdAndId(currentUser.getId(), id)
        .orElseThrow(() -> new IllegalArgumentException("Order not found"));

    return orderMapper.toDto(order);
  }

  private AppUser getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return appUserRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
  }

  private Map<Long, Integer> aggregateQuantities(List<CreateOrderItemInput> items) {
    Map<Long, Integer> quantities = new HashMap<>();

    for (CreateOrderItemInput item : items) {
      if (item.dishId() == null) {
        throw new IllegalArgumentException("Dish id is required");
      }
      if (item.quantity() == null || item.quantity() <= 0) {
        throw new IllegalArgumentException("Quantity must be greater than zero");
      }

      quantities.merge(item.dishId(), item.quantity(), (current, next) -> current + next);
    }

    return quantities;
  }

}
