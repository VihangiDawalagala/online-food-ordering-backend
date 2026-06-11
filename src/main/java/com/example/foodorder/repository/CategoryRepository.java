package com.example.foodorder.repository;

import com.example.foodorder.entity.Category;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {
}