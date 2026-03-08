package com.fitme.backend.service.impl;

import com.fitme.backend.entity.Restaurant;
import com.fitme.backend.repository.RestaurantRepository;
import com.fitme.backend.service.interfaces.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {

  private final RestaurantRepository restaurantRepository;

  @Override
  public List<Restaurant> getAll() {
    return restaurantRepository.findAll();
  }

  @Override
  public Restaurant getById(Long id) {
    return restaurantRepository.findById(id).orElse(null);
  }

  @Override
  public Restaurant save(Restaurant restaurant) {
    return restaurantRepository.save(restaurant);
  }

  @Override
  public Restaurant getRestaurantById(Long id) {
    return restaurantRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
  }
}
