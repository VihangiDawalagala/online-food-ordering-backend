package com.example.foodorder.service;

import com.example.foodorder.entity.*;
import com.example.foodorder.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final CartItemRepository cartItemRepository;

    public Cart getCartByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return cartRepository.findByUser(user)
                .orElseGet(() -> {

                    Cart cart = Cart.builder()
                            .user(user)
                            .build();

                    return cartRepository.save(cart);
                });
    }

    public Cart addFoodToCart(
            Long userId,
            Long foodId,
            Integer quantity
    ) {

        Cart cart = getCartByUser(userId);

        FoodItem foodItem = foodRepository.findById(foodId)
                .orElseThrow(() ->
                        new RuntimeException("Food not found"));

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .foodItem(foodItem)
                .quantity(quantity)
                .build();

        cartItemRepository.save(cartItem);

        cart.getCartItems().add(cartItem);

        return cartRepository.save(cart);
    }
}