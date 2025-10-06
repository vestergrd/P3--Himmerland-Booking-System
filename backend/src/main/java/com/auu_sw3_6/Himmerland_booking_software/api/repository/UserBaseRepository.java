package com.auu_sw3_6.Himmerland_booking_software.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.auu_sw3_6.Himmerland_booking_software.api.model.User;

@NoRepositoryBean  // Ensures Spring does not try to instantiate this directly
public interface UserBaseRepository<T extends User> extends JpaRepository<T, Long> {
    Optional<T> findByUsername(String username);
}