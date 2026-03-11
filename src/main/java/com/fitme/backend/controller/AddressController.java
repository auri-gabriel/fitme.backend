package com.fitme.backend.controller;

import com.fitme.backend.dto.CreateAddressInput;
import com.fitme.backend.dto.UserAddressDto;
import com.fitme.backend.service.interfaces.AddressService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class AddressController {

  private final AddressService addressService;

  public AddressController(AddressService addressService) {
    this.addressService = addressService;
  }

  @MutationMapping
  @PreAuthorize("isAuthenticated()")
  public UserAddressDto createMyAddress(@Argument CreateAddressInput input) {
    return addressService.createMyAddress(input);
  }

  @QueryMapping
  @PreAuthorize("isAuthenticated()")
  public List<UserAddressDto> getMyAddresses() {
    return addressService.getMyAddresses();
  }

  @MutationMapping
  @PreAuthorize("isAuthenticated()")
  public UserAddressDto setDefaultAddress(@Argument Long addressId) {
    return addressService.setDefaultAddress(addressId);
  }

  @MutationMapping
  @PreAuthorize("isAuthenticated()")
  public Boolean deleteMyAddress(@Argument Long addressId) {
    return addressService.deleteMyAddress(addressId);
  }
}
