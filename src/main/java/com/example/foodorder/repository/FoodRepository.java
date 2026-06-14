package com.example.foodorder.repository;

import com.example.foodorder.entity.FoodItem;
import com.example.foodorder.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<FoodItem, Long> {

    List<FoodItem> findByNameIgnoreCase(String name);

    List<FoodItem> findByCategory(Category category);
}
