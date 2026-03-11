package com.fitme.backend.service.interfaces;

import com.fitme.backend.dto.CreateAddressInput;
import com.fitme.backend.dto.UserAddressDto;

import java.util.List;

public interface AddressService {
  UserAddressDto createMyAddress(CreateAddressInput input);

  List<UserAddressDto> getMyAddresses();

  UserAddressDto setDefaultAddress(Long addressId);

  boolean deleteMyAddress(Long addressId);
}
