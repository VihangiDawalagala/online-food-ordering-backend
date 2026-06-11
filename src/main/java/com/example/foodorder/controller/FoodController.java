package com.example.foodorder.controller;

import com.example.foodorder.entity.FoodItem;
import com.example.foodorder.service.FoodService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FoodController {

    private final FoodService foodService;

    @PostMapping
    public ResponseEntity<FoodItem> createFood(
            @RequestBody FoodItem foodItem) {

        return ResponseEntity.ok(
                foodService.saveFood(foodItem)
        );
    }

    @GetMapping
    public ResponseEntity<List<FoodItem>> getAllFoods() {

        return ResponseEntity.ok(
                foodService.getAllFoods()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodItem> getFoodById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                foodService.getFoodById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFood(
            @PathVariable Long id) {

        foodService.deleteFood(id);

        return ResponseEntity.ok("Food deleted successfully");
    }
}