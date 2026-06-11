package com.example.foodorder.controller;

import com.example.foodorder.entity.Cart;
import com.example.foodorder.service.CartService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(
            @PathVariable Long userId
    ) {

        return ResponseEntity.ok(
                cartService.getCartByUser(userId)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(
            @RequestBody Map<String, Object> request
    ) {

        Long userId = Long.valueOf(
                request.get("userId").toString()
        );

        Long foodId = Long.valueOf(
                request.get("foodId").toString()
        );

        Integer quantity = Integer.valueOf(
                request.get("quantity").toString()
        );

        return ResponseEntity.ok(
                cartService.addFoodToCart(
                        userId,
                        foodId,
                        quantity
                )
        );
    }
}