package com.example.foodorder.controller;

import com.example.foodorder.entity.Payment;
import com.example.foodorder.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/process")
    public ResponseEntity<Payment> processPayment(
            @RequestBody Map<String, Object> request
    ) {

        Long orderId = Long.valueOf(
                request.get("orderId").toString()
        );

        Double amount = Double.valueOf(
                request.get("amount").toString()
        );

        return ResponseEntity.ok(
                paymentService.processPayment(
                        orderId,
                        amount
                )
        );
    }
}