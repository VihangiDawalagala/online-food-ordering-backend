package com.example.foodorder.repository;

import com.example.foodorder.entity.Cart;
import com.example.foodorder.entity.CartItem;
import com.example.foodorder.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartAndFoodItem(
            Cart cart,
            FoodItem foodItem
    );
}