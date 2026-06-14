package com.example.foodorder.service;

import com.example.foodorder.entity.*;
import com.example.foodorder.exception.BadRequestException;
import com.example.foodorder.exception.ResourceNotFoundException;
import com.example.foodorder.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;

    public Order placeOrder(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart not found"));

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PLACED)
                .build();

        double total = 0;

        for (CartItem cartItem : cart.getCartItems()) {

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .foodItem(cartItem.getFoodItem())
                    .quantity(cartItem.getQuantity())
                    .build();

            order.getOrderItems().add(orderItem);

            total += cartItem.getFoodItem().getPrice()
                    * cartItem.getQuantity();
        }

        Payment payment = Payment.builder()
                .amount(total)
                .status(PaymentStatus.PENDING)
                .order(order)
                .build();

        order.setPayment(payment);

        cart.getCartItems().clear();

        cartRepository.save(cart);

        Order savedOrder = orderRepository.save(order);
        log.info("Order placed with id {} for user {}", savedOrder.getId(), userId);

        return savedOrder;
    }

    public List<Order> getUserOrders(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        return orderRepository.findByUser(user);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order updateStatus(
            Long orderId,
            OrderStatus status
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        order.setStatus(status);

        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, status);

        return updatedOrder;
    }
}
