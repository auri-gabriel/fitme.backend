package com.fitme.backend.service.impl;

import com.fitme.backend.entity.Restaurant;
import com.fitme.backend.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantServiceImplTest {

  @Mock
  private RestaurantRepository restaurantRepository;

  @InjectMocks
  private RestaurantServiceImpl restaurantService;

  @Test
  void getAll_ShouldReturnAllRestaurants() {
    Restaurant restaurant1 = mock(Restaurant.class);
    Restaurant restaurant2 = mock(Restaurant.class);
    List<Restaurant> expected = List.of(restaurant1, restaurant2);

    when(restaurantRepository.findAll()).thenReturn(expected);

    List<Restaurant> result = restaurantService.getAll();

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(expected, result);
    verify(restaurantRepository, times(1)).findAll();
  }

  @Test
  void getById_WhenRestaurantExists_ShouldReturnRestaurant() {
    Long id = 1L;
    Restaurant expected = mock(Restaurant.class);

    when(restaurantRepository.findById(id)).thenReturn(Optional.of(expected));

    Restaurant result = restaurantService.getById(id);

    assertNotNull(result);
    assertEquals(expected, result);
    verify(restaurantRepository, times(1)).findById(id);
  }

  @Test
  void getById_WhenRestaurantDoesNotExist_ShouldReturnNull() {
    Long id = 999L;

    when(restaurantRepository.findById(id)).thenReturn(Optional.empty());

    Restaurant result = restaurantService.getById(id);

    assertNull(result);
    verify(restaurantRepository, times(1)).findById(id);
  }

  @Test
  void save_ShouldPersistAndReturnRestaurant() {
    Restaurant input = mock(Restaurant.class);
    Restaurant saved = mock(Restaurant.class);

    when(restaurantRepository.save(input)).thenReturn(saved);

    Restaurant result = restaurantService.save(input);

    assertNotNull(result);
    assertEquals(saved, result);
    verify(restaurantRepository, times(1)).save(input);
  }

  @Test
  void getRestaurantById_WhenRestaurantExists_ShouldReturnRestaurant() {
    Long id = 10L;
    Restaurant expected = mock(Restaurant.class);

    when(restaurantRepository.findById(id)).thenReturn(Optional.of(expected));

    Restaurant result = restaurantService.getRestaurantById(id);

    assertNotNull(result);
    assertEquals(expected, result);
    verify(restaurantRepository, times(1)).findById(id);
  }

  @Test
  void getRestaurantById_WhenRestaurantDoesNotExist_ShouldThrowRuntimeExceptionWithMessage() {
    Long id = 404L;

    when(restaurantRepository.findById(id)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
        RuntimeException.class,
        () -> restaurantService.getRestaurantById(id));

    assertEquals("Restaurant not found with id: " + id, exception.getMessage());
    verify(restaurantRepository, times(1)).findById(id);
  }
}
