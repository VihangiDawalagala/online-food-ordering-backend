package com.example.foodorder.controller;

import com.example.foodorder.dto.OrderStatusRequest;
import com.example.foodorder.entity.Order;
import com.example.foodorder.entity.Role;
import com.example.foodorder.entity.User;
import com.example.foodorder.exception.ResourceNotFoundException;
import com.example.foodorder.repository.UserRepository;
import com.example.foodorder.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(
            @RequestBody Map<String, Long> request,
            Authentication authentication
    ) {

        Long userId = request.get("userId");
        verifyUserAccess(userId, authentication);

        return ResponseEntity.ok(
                orderService.placeOrder(
                        userId
                )
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Order>> getOrders(
            @PathVariable Long userId,
            Authentication authentication
    ) {

        verifyUserAccess(userId, authentication);

        return ResponseEntity.ok(
                orderService.getUserOrders(userId)
        );
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody OrderStatusRequest request
    ) {

        return ResponseEntity.ok(
                orderService.updateStatus(
                        id,
                        request.getStatus()
                )
        );
    }

    private void verifyUserAccess(
            Long userId,
            Authentication authentication
    ) {

        User currentUser = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Authenticated user not found"));

        if (currentUser.getRole() != Role.ADMIN &&
                !currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("You can only access your own orders");
        }
    }
}
