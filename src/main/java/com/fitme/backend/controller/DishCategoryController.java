package com.fitme.backend.controller;

import com.fitme.backend.dto.CreateDishCategoryInput;
import com.fitme.backend.entity.DishCategory;
import com.fitme.backend.entity.Restaurant;
import com.fitme.backend.service.interfaces.DishCategoryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DishCategoryController {

    private final DishCategoryService categoryService;

    public DishCategoryController(DishCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @MutationMapping
    public DishCategory createDishCategory(@Argument CreateDishCategoryInput input) {
        return categoryService.create(input);
    }

    @SchemaMapping(typeName = "Restaurant", field = "categories")
    public List<DishCategory> getCategories(Restaurant restaurant) {
        return categoryService.getCategoriesByRestaurant(restaurant.getId());
    }
}
