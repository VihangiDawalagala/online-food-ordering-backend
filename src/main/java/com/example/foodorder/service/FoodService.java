package com.example.foodorder.service;

import com.example.foodorder.entity.FoodItem;
import com.example.foodorder.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FoodService {

    private final FoodRepository foodRepository;

    public FoodItem saveFood(FoodItem foodItem) {
        return foodRepository.save(foodItem);
    }

    public List<FoodItem> getAllFoods() {
        return foodRepository.findAll();
    }

    public FoodItem getFoodById(Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Food not found"));
    }

    public void deleteFood(Long id) {
        foodRepository.deleteById(id);
    }
}