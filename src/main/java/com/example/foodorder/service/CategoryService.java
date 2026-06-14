package com.example.foodorder.service;

import com.example.foodorder.entity.Category;
import com.example.foodorder.exception.BadRequestException;
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

        normalizeAndValidateCategoryName(category, null);

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

        normalizeAndValidateCategoryName(category, id);

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

    private void normalizeAndValidateCategoryName(
            Category category,
            Long existingCategoryId
    ) {

        if (category.getName() == null || category.getName().isBlank()) {
            throw new BadRequestException("Category name is required");
        }

        String normalizedName = category.getName().trim();
        boolean duplicateExists = existingCategoryId == null
                ? categoryRepository.existsByNormalizedName(normalizedName)
                : categoryRepository.existsByNormalizedNameAndIdNot(
                        normalizedName,
                        existingCategoryId
                );

        if (duplicateExists) {
            throw new BadRequestException("Category already exists");
        }

        category.setName(normalizedName);
    }
}
