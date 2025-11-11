package com.sds2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.hotel.Hotel;
import com.sds2.classes.hotel.HotelOffer;

@Repository
public interface HotelOfferRepository extends JpaRepository<HotelOffer, Long> {
    List<HotelOffer> findByHotelAndAdults(Hotel hotel, int adults);
}
