package com.fitme.backend.repository;

import com.fitme.backend.entity.AppUserAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppUserAddressRepository extends JpaRepository<AppUserAddress, Long> {
  List<AppUserAddress> findByAppUserIdOrderByIsDefaultDescCreatedAtDesc(Long appUserId);

  Optional<AppUserAddress> findByAppUserIdAndId(Long appUserId, Long id);

  long countByAppUserId(Long appUserId);
}
