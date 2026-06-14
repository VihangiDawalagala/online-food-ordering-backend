package com.example.foodorder.repository;

import com.example.foodorder.entity.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository
        extends JpaRepository<Category, Long> {

    @Query("select count(c) > 0 from Category c where lower(trim(c.name)) = lower(trim(:name))")
    boolean existsByNormalizedName(@Param("name") String name);

    @Query("""
            select count(c) > 0 from Category c
            where lower(trim(c.name)) = lower(trim(:name))
            and c.id <> :id
            """)
    boolean existsByNormalizedNameAndIdNot(
            @Param("name") String name,
            @Param("id") Long id
    );
}
