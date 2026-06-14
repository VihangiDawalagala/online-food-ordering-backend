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
public class CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final FoodRepository foodRepository;
    private final CartItemRepository cartItemRepository;

    public Cart getCartByUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

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

        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        FoodItem foodItem = foodRepository.findById(foodId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Food not found"));

        CartItem existingItem = cart.getCartItems()
                .stream()
                .filter(item ->
                        item.getFoodItem().getId()
                                .equals(foodId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {

            existingItem.setQuantity(
                    existingItem.getQuantity() + quantity
            );

            cartItemRepository.save(existingItem);

        } else {

            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .foodItem(foodItem)
                    .quantity(quantity)
                    .build();

            cartItemRepository.save(cartItem);

            cart.getCartItems().add(cartItem);
        }

        Cart savedCart = cartRepository.save(cart);
        log.info(
                "Food item {} added to cart for user {} with quantity {}",
                foodId,
                userId,
                quantity
        );

        return savedCart;
    }

    public void removeCartItem(Long cartItemId) {
        if (!cartItemRepository.existsById(cartItemId)) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        cartItemRepository.deleteById(cartItemId);
        log.info("Cart item removed with id {}", cartItemId);
    }

    public Cart updateCartItemQuantity(
            Long cartItemId,
            Integer quantity
    ) {

        if (quantity == null || quantity <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cart item not found"));

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        Cart savedCart = cartRepository.save(cartItem.getCart());
        log.info(
                "Cart item {} quantity updated to {}",
                cartItemId,
                quantity
        );

        return savedCart;
    }
}
