package com.example.foodorder.service;

import com.example.foodorder.entity.*;
import com.example.foodorder.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public Payment processPayment(
            Long orderId,
            Double amount
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .status(PaymentStatus.COMPLETED)
                .build();

        return paymentRepository.save(payment);
    }
}