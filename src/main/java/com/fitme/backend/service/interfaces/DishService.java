package com.fitme.backend.service.interfaces;

import com.fitme.backend.entity.Dish;

import java.util.List;

public interface DishService {

  Dish save(Dish dish);

  List<Dish> getByRestaurantId(Long restaurantId);
}
