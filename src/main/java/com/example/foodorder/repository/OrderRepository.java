package com.example.foodorder.repository;

import com.example.foodorder.entity.Order;
import com.example.foodorder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);

}