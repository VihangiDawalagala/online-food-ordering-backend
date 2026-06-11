package com.example.foodorder.controller;

import com.example.foodorder.entity.Order;
import com.example.foodorder.service.OrderService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(
            @RequestBody Map<String, Long> request
    ) {

        return ResponseEntity.ok(
                orderService.placeOrder(
                        request.get("userId")
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getOrders(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                orderService.getUserOrders(userId)
        );
    }
}