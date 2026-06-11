package com.example.foodorder.repository;

import com.example.foodorder.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodRepository extends JpaRepository<FoodItem, Long> {
}