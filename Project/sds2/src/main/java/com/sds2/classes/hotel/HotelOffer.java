package com.sds2.classes.hotel;

import com.sds2.classes.Price;
import com.sds2.classes.Room;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class HotelOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String offerId;
    private String hotelId;
    private String checkInDate;
    private String checkOutDate;
    @Embedded
    private Room room;
    @Embedded
    private Price price;
    private int adults;

    public HotelOffer() {}

    public HotelOffer 
    (
        String offerId, 
        String hotelId, 
        String checkInDate, 
        String checkOutDate, 
        Room room, 
        Price price,
        int adults
        ) {
        this.offerId = offerId;
        this.hotelId = hotelId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.room = room;
        this.price = price;
        this.adults = adults;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }
}



// "offers":[{"id":"N8D7VAMA3W","checkInDate":"2025-11-11","checkOutDate":"2025-11-12",
// "rateCode":"PRO","rateFamilyEstimated":{"code":"PRO","type":"P"},
// "commission":{"percentage":"4.00"},"boardType":"BREAKFAST",
// "room":{"type":"ROH","typeEstimated":{"category":"STANDARD_ROOM"},
// "description":{"text":"HRS-Rate - LastMinute-Discount\nStandard room A standard room consists of a room with shower-toilet or bathtub-toilet.","lang":"EN"}},


// "guests":{"adults":1},"price":{"currency":"PLN","total":"243.00",
// "taxes":[{"code":"VALUE_ADDED_TAX","percentage":"8.00","included":true},
// {"code":"SERVICE_CHARGE","percentage":"23.00","included":true}],
// "variations":{"average":{"total":"243.00"},
// "changes":[{"startDate":"2025-11-11","endDate":"2025-11-12","total":"243.00"}]}},
// "policies":{"cancellations":[{"numberOfNights":1,"deadline":"2025-11-11T17:00:00+01:00",
// "description":{"text":"The cancellation policy only applies for guaranteed bookings. The cancellation policy only applies for guaranteed bookings."},
// "policyType":"CANCELLATION"}],
// "holdTime":{"deadline":"2025-11-11T18:00:00"},"paymentType":"holdTime",
// "refundable":{"cancellationRefund":"REFUNDABLE_UP_TO_DEADLINE"},
// "lengthOfStay":{"minimumLengthOfStay":1}},"self":"https://api.amadeus.com/v3/shopping/hotel-offers/N8D7VAMA3W",
// "roomInformation":{"description":"HRS-Rate - LastMinute-Discount\nStandard room A standard room consists of a room with shower-toilet or bathtub-toilet.",
// "type":"ROH","typeEstimated":{"category":"STANDARD_ROOM"}}}],
// "self":"https://api.amadeus.com/v3/shopping/hotel-offers?hotelIds=HSPOZAGQ&adults=1"}