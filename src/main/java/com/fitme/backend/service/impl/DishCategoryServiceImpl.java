package com.fitme.backend.service.impl;

import com.fitme.backend.dto.CreateDishCategoryInput;
import com.fitme.backend.entity.DishCategory;
import com.fitme.backend.entity.Restaurant;
import com.fitme.backend.repository.DishCategoryRepository;
import com.fitme.backend.repository.RestaurantRepository;
import com.fitme.backend.service.interfaces.DishCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishCategoryServiceImpl implements DishCategoryService {

  private final DishCategoryRepository categoryRepository;
  private final RestaurantRepository restaurantRepository;

  @Override
  public DishCategory create(CreateDishCategoryInput input) {
    Restaurant restaurant = restaurantRepository.findById(input.restaurantId())
        .orElseThrow(() -> new RuntimeException("Restaurant not found"));

    DishCategory category = DishCategory.builder()
        .name(input.name())
        .restaurant(restaurant)
        .build();

    return categoryRepository.save(category);
  }

  @Override
  public List<DishCategory> getCategoriesByRestaurant(Long restaurantId) {
    return categoryRepository.findByRestaurantId(restaurantId);
  }
}
