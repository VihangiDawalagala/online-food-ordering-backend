package com.example.foodorder.config;

import com.example.foodorder.entity.Category;
import com.example.foodorder.entity.FoodItem;
import com.example.foodorder.repository.CategoryRepository;
import com.example.foodorder.repository.FoodRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SampleDataInitializer implements ApplicationRunner {

    private final CategoryRepository categoryRepository;
    private final FoodRepository foodRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Category pizza = getOrCreateUniqueCategory(
                "Pizza",
                "Pizza menu items"
        );
        Category burger = getOrCreateUniqueCategory(
                "Burger",
                "Burger menu items"
        );
        Category friedRice = getOrCreateUniqueCategory(
                "Fried Rice",
                "Fried rice menu items"
        );

        moveFoodsToCategory("Chicken Pizza", pizza);
        moveFoodsToCategory("Burger Deluxe", burger);
        moveFoodsToCategory("Chicken Fried Rice", friedRice);
    }

    private Category getOrCreateUniqueCategory(
            String name,
            String description
    ) {

        List<Category> matchingCategories =
                categoryRepository.findAllByNormalizedName(name);

        if (matchingCategories.isEmpty()) {
            Category category = Category.builder()
                    .name(name)
                    .description(description)
                    .build();

            Category savedCategory = categoryRepository.save(category);
            log.info("Sample category created: {}", name);
            return savedCategory;
        }

        Category canonicalCategory = matchingCategories.get(0);
        canonicalCategory.setName(name);
        canonicalCategory.setDescription(description);

        for (int index = 1; index < matchingCategories.size(); index++) {
            Category duplicateCategory = matchingCategories.get(index);

            List<FoodItem> duplicateFoods =
                    foodRepository.findByCategory(duplicateCategory);
            duplicateFoods.forEach(food ->
                    food.setCategory(canonicalCategory)
            );
            foodRepository.saveAll(duplicateFoods);

            categoryRepository.delete(duplicateCategory);
            log.info(
                    "Duplicate sample category removed: {} with id {}",
                    duplicateCategory.getName(),
                    duplicateCategory.getId()
            );
        }

        return categoryRepository.save(canonicalCategory);
    }

    private void moveFoodsToCategory(
            String foodName,
            Category category
    ) {

        List<FoodItem> foods = foodRepository.findByNameIgnoreCase(foodName);
        foods.forEach(food ->
                food.setCategory(category)
        );
        foodRepository.saveAll(foods);
    }
}
