package com.sds2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.hotel.Hotel;
import com.sds2.dto.HotelDTO;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {
    HotelDTO findById(long id);
    List<Hotel> findByIataCode(String iataCode);
    Hotel findByHotelId(String hotelId);
    List<Hotel> findByHotelIdIn(Iterable<String> hotelIds);
    List<Hotel> findByAddress_CityNameIgnoreCaseAndAddress_CountryCodeIgnoreCase(String cityName, String countryCode);
    List<Hotel> findByAddress_CityNameIgnoreCase(String cityName);
    List<Hotel> findByAddress_CountryCodeIgnoreCase(String countryCode);
    List<Hotel> findByCoordinates_LatitudeAndCoordinates_Longitude(double latitude, double longitude);
}
