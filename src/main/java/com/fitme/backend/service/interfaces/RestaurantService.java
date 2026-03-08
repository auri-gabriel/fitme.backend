package com.fitme.backend.service.interfaces;

import com.fitme.backend.entity.Restaurant;

import java.util.List;

public interface RestaurantService {

  List<Restaurant> getAll();

  Restaurant getById(Long id);

  Restaurant save(Restaurant restaurant);

  Restaurant getRestaurantById(Long id);
}
