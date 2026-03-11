package com.fitme.backend.service.impl;

import com.fitme.backend.dto.CreateAddressInput;
import com.fitme.backend.dto.UserAddressDto;
import com.fitme.backend.entity.AppUser;
import com.fitme.backend.entity.AppUserAddress;
import com.fitme.backend.repository.AppUserAddressRepository;
import com.fitme.backend.repository.AppUserRepository;
import com.fitme.backend.service.interfaces.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@SuppressWarnings("null")
public class AddressServiceImpl implements AddressService {

  private final AppUserAddressRepository appUserAddressRepository;
  private final AppUserRepository appUserRepository;

  public AddressServiceImpl(
      AppUserAddressRepository appUserAddressRepository,
      AppUserRepository appUserRepository) {
    this.appUserAddressRepository = appUserAddressRepository;
    this.appUserRepository = appUserRepository;
  }

  @Override
  @Transactional
  public UserAddressDto createMyAddress(CreateAddressInput input) {
    if (input == null) {
      throw new IllegalArgumentException("Input is required");
    }
    if (input.label() == null || input.label().isBlank()) {
      throw new IllegalArgumentException("Address label is required");
    }
    if (input.line1() == null || input.line1().isBlank()) {
      throw new IllegalArgumentException("Address line1 is required");
    }
    if (input.city() == null || input.city().isBlank()) {
      throw new IllegalArgumentException("City is required");
    }
    if (input.postalCode() == null || input.postalCode().isBlank()) {
      throw new IllegalArgumentException("Postal code is required");
    }

    AppUser currentUser = getCurrentUser();
    boolean makeDefault = Boolean.TRUE.equals(input.isDefault())
        || appUserAddressRepository.countByAppUserId(currentUser.getId()) == 0;

    if (makeDefault) {
      List<AppUserAddress> existingAddresses = appUserAddressRepository
          .findByAppUserIdOrderByIsDefaultDescCreatedAtDesc(currentUser.getId());
      for (AppUserAddress address : existingAddresses) {
        if (Boolean.TRUE.equals(address.getIsDefault())) {
          address.setIsDefault(false);
        }
      }
      appUserAddressRepository.saveAll(existingAddresses);
    }

    AppUserAddress saved = appUserAddressRepository.save(
        AppUserAddress.builder()
            .appUser(currentUser)
            .label(input.label().trim())
            .line1(input.line1().trim())
            .line2(input.line2() != null ? input.line2().trim() : null)
            .city(input.city().trim())
            .postalCode(input.postalCode().trim())
            .isDefault(makeDefault)
            .build());

    return toDto(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserAddressDto> getMyAddresses() {
    AppUser currentUser = getCurrentUser();
    return appUserAddressRepository.findByAppUserIdOrderByIsDefaultDescCreatedAtDesc(currentUser.getId())
        .stream()
        .map(this::toDto)
        .toList();
  }

  @Override
  @Transactional
  public UserAddressDto setDefaultAddress(Long addressId) {
    if (addressId == null) {
      throw new IllegalArgumentException("Address id is required");
    }

    AppUser currentUser = getCurrentUser();
    AppUserAddress selectedAddress = appUserAddressRepository
        .findByAppUserIdAndId(currentUser.getId(), addressId)
        .orElseThrow(() -> new IllegalArgumentException("Address not found"));

    List<AppUserAddress> addresses = appUserAddressRepository.findByAppUserId(currentUser.getId());
    for (AppUserAddress address : addresses) {
      address.setIsDefault(address.getId().equals(selectedAddress.getId()));
    }
    appUserAddressRepository.saveAll(addresses);

    return toDto(selectedAddress);
  }

  @Override
  @Transactional
  public boolean deleteMyAddress(Long addressId) {
    if (addressId == null) {
      throw new IllegalArgumentException("Address id is required");
    }

    AppUser currentUser = getCurrentUser();
    AppUserAddress address = appUserAddressRepository
        .findByAppUserIdAndId(currentUser.getId(), addressId)
        .orElseThrow(() -> new IllegalArgumentException("Address not found"));

    boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());
    appUserAddressRepository.delete(address);

    if (wasDefault) {
      List<AppUserAddress> remainingAddresses = appUserAddressRepository
          .findByAppUserIdOrderByIsDefaultDescCreatedAtDesc(currentUser.getId());
      if (!remainingAddresses.isEmpty()) {
        AppUserAddress fallback = remainingAddresses.get(0);
        fallback.setIsDefault(true);
        appUserAddressRepository.save(fallback);
      }
    }

    return true;
  }

  private AppUser getCurrentUser() {
    String username = SecurityContextHolder.getContext().getAuthentication().getName();
    return appUserRepository.findByUsername(username)
        .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));
  }

  private UserAddressDto toDto(AppUserAddress address) {
    return new UserAddressDto(
        address.getId(),
        address.getLabel(),
        address.getLine1(),
        address.getLine2(),
        address.getCity(),
        address.getPostalCode(),
        address.getIsDefault(),
        address.getCreatedAt() != null ? address.getCreatedAt().toString() : null,
        address.getUpdatedAt() != null ? address.getUpdatedAt().toString() : null);
  }
}
