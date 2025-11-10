package com.sds2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sds2.classes.hotel.HotelOffer;

@Repository
public interface HotelOfferRepository extends JpaRepository<HotelOffer, Long> {
    HotelOffer findByHotelIdAndAdults(String hotelId, int adults);
}
