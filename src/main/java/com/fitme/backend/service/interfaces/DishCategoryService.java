package com.fitme.backend.service.interfaces;

import com.fitme.backend.dto.CreateDishCategoryInput;
import com.fitme.backend.entity.DishCategory;

import java.util.List;

public interface DishCategoryService {

  DishCategory create(CreateDishCategoryInput input);

  List<DishCategory> getCategoriesByRestaurant(Long restaurantId);
}
