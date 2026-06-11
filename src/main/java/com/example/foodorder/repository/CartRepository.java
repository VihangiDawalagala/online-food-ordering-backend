package com.example.foodorder.repository;

import com.example.foodorder.entity.Cart;
import com.example.foodorder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

}