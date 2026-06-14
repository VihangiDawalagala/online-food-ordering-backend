package com.example.foodorder.service;

import com.example.foodorder.entity.Category;
import com.example.foodorder.exception.ResourceNotFoundException;
import com.example.foodorder.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category createCategory(
            Category category
    ) {

        Category savedCategory = categoryRepository.save(category);
        log.info("Category created with id {}", savedCategory.getId());
        return savedCategory;
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(
            Long id,
            Category category
    ) {

        Category existingCategory =
                categoryRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Category not found"));

        existingCategory.setName(
                category.getName()
        );

        existingCategory.setDescription(
                category.getDescription()
        );

        Category updatedCategory = categoryRepository.save(
                existingCategory
        );
        log.info("Category updated with id {}", updatedCategory.getId());

        return updatedCategory;
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }

        categoryRepository.deleteById(id);
        log.info("Category deleted with id {}", id);
    }
}
