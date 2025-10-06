package com.auu_sw3_6.Himmerland_booking_software.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.auu_sw3_6.Himmerland_booking_software.api.model.Booking;
import com.auu_sw3_6.Himmerland_booking_software.api.model.Resource;
import com.auu_sw3_6.Himmerland_booking_software.api.model.modelEnum.BookingStatus;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByResourceAndStatus(Resource resource, BookingStatus status);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findByResource(Resource resource);
    List<Booking> findByResource_Id(Long resourceId);
    List<Booking> findByUser_Id(long userId);
}