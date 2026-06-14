package com.example.foodorder.service;

import com.example.foodorder.entity.*;
import com.example.foodorder.exception.BadRequestException;
import com.example.foodorder.exception.ResourceNotFoundException;
import com.example.foodorder.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public Payment processPayment(
            Long orderId,
            Double amount
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Order not found"));

        if (amount == null || amount <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero");
        }

        Payment payment = order.getPayment();

        if (payment == null) {
            payment = Payment.builder()
                    .order(order)
                    .build();
            order.setPayment(payment);
        }

        payment.setAmount(amount);
        payment.setStatus(PaymentStatus.COMPLETED);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment completed for order {} with amount {}", orderId, amount);

        return savedPayment;
    }
}
