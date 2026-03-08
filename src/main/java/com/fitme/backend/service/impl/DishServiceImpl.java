package com.fitme.backend.service.impl;

import com.fitme.backend.entity.Dish;
import com.fitme.backend.repository.DishRepository;
import com.fitme.backend.service.interfaces.DishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DishServiceImpl implements DishService {

  private final DishRepository dishRepository;

  @Override
  public Dish save(Dish dish) {
    return dishRepository.save(dish);
  }

  @Override
  public List<Dish> getByRestaurantId(Long restaurantId) {
    return dishRepository.findByRestaurantId(restaurantId);
  }
}
