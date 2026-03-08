package com.fitme.backend.controller;

import com.fitme.backend.dto.CreateDishInput;
import com.fitme.backend.entity.Dish;
import com.fitme.backend.entity.Restaurant;
import com.fitme.backend.service.interfaces.DishService;
import com.fitme.backend.service.interfaces.RestaurantService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class DishController {

    private final DishService dishService;
    private final RestaurantService restaurantService;

    public DishController(DishService dishService, RestaurantService restaurantService) {
        this.dishService = dishService;
        this.restaurantService = restaurantService;
    }

    @MutationMapping
    public Dish createDish(@Argument CreateDishInput input) {
        Restaurant restaurant = restaurantService.getById(input.restaurantId());
        if (restaurant == null) {
            throw new RuntimeException("Restaurant not found");
        }

        Dish dish = Dish.builder()
                .name(input.name())
                .price(input.price())
                .image(input.image())
                .description(input.description())
                .restaurant(restaurant)
                .build();

        return dishService.save(dish);
    }
}
