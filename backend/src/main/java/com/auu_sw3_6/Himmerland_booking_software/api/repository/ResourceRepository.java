package com.auu_sw3_6.Himmerland_booking_software.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Resource;

@NoRepositoryBean 
public interface ResourceRepository<T extends Resource> extends JpaRepository<T, Long> {
}