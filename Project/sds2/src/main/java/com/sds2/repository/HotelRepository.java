package com.sds2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.hotel.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    List<Hotel> findByHotelId(String hotelId);
    List<Hotel> findByHotelIdIn(Iterable<String> hotelIds);
    List<Hotel> findByCityNameOrCountryCode(String cityName, String countryCode);
    List<Hotel> findByCoordinates_LatitudeAndCoordinates_Longitude(double latitude, double longitude);
}
