package com.example.foodorder.service;

import com.example.foodorder.entity.Category;
import com.example.foodorder.entity.FoodItem;
import com.example.foodorder.exception.BadRequestException;
import com.example.foodorder.exception.ResourceNotFoundException;
import com.example.foodorder.repository.CategoryRepository;
import com.example.foodorder.repository.FoodRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodService {

    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;

    public FoodItem saveFood(FoodItem foodItem) {
        foodItem.setCategory(resolveCategory(foodItem));
        FoodItem savedFood = foodRepository.save(foodItem);
        log.info("Food item created with id {}", savedFood.getId());
        return savedFood;
    }

    public List<FoodItem> getAllFoods() {
        return foodRepository.findAll();
    }

    public FoodItem getFoodById(Long id) {
        return foodRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Food not found"));
    }

    public FoodItem updateFood(
            Long id,
            FoodItem foodItem
    ) {

        FoodItem existingFood =
                foodRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Food not found"));

        existingFood.setName(foodItem.getName());
        existingFood.setDescription(foodItem.getDescription());
        existingFood.setPrice(foodItem.getPrice());
        existingFood.setImageUrl(foodItem.getImageUrl());
        existingFood.setStatus(foodItem.getStatus());
        existingFood.setCategory(resolveCategory(foodItem));

        FoodItem updatedFood = foodRepository.save(existingFood);
        log.info("Food item updated with id {}", updatedFood.getId());
        return updatedFood;
    }

    public void deleteFood(Long id) {
        if (!foodRepository.existsById(id)) {
            throw new ResourceNotFoundException("Food not found");
        }

        foodRepository.deleteById(id);
        log.info("Food item deleted with id {}", id);
    }

    private Category resolveCategory(FoodItem foodItem) {
        if (foodItem.getCategory() == null ||
                foodItem.getCategory().getId() == null) {
            throw new BadRequestException("Category id is required");
        }

        return categoryRepository.findById(foodItem.getCategory().getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Category not found"));
    }
}
