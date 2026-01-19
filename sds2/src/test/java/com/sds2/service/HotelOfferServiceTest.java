package com.sds2.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sds2.WebClientMockedTestClasse;
import com.sds2.classes.response.HotelOfferResponse;
import com.sds2.classes.response.HotelOfferResponse.HotelOfferData;
import com.sds2.classes.response.HotelOfferResponse.OfferGuests;
import com.sds2.classes.response.HotelOfferResponse.OfferItem;
import com.sds2.classes.response.HotelOfferResponse.OfferPrice;
import com.sds2.classes.response.HotelOfferResponse.OfferRoom;
import com.sds2.classes.response.HotelOfferResponse.OfferRoomDescription;
import com.sds2.classes.response.HotelOfferResponse.OfferRoomTypeEstimated;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class HotelOfferServiceTest extends WebClientMockedTestClasse {
    @InjectMocks
    private HotelOfferService hotelOfferService;

    @Test
    void testGetOffersByHotelId() {
        HotelOfferResponse exampleHotelOfferReponse = getExampleOfferHotelResponse();
        when(responseSpec.bodyToMono(HotelOfferResponse.class)).thenReturn(Mono.just(exampleHotelOfferReponse));

        hotelOfferService.getOffersByHotelId("hello", 1, "hello", "hello");;
    }

    HotelOfferResponse getExampleOfferHotelResponse(){

        OfferGuests offerGuests = new OfferGuests();
        offerGuests.setAdults(1);
    
        OfferPrice offerPrice = new OfferPrice();
        offerPrice.setCurrency("PLN");
        offerPrice.setTotal("hello");

        OfferRoomTypeEstimated offerRoomTypeEstimated = new OfferRoomTypeEstimated();
        offerRoomTypeEstimated.setCategory("hello");

        OfferRoomDescription offerRoomDescription = new OfferRoomDescription();
        offerRoomDescription.setLang("hello");
        offerRoomDescription.setText("hello");

        OfferRoom offerRoom = new OfferRoom();
        offerRoom.setTypeEstimated((offerRoomTypeEstimated));
        offerRoom.setDescription(offerRoomDescription);

        OfferItem offerItem = new OfferItem();
        offerItem.setCheckInDate("hello");
        offerItem.setCheckOutDate("hello");
        offerItem.setGuests(offerGuests);
        offerItem.setPrice(offerPrice);
        offerItem.setRoom(offerRoom);

        HotelOfferData hotelOfferData = new HotelOfferData();
        hotelOfferData.setOffers(List.of(offerItem));

        HotelOfferResponse hotelOfferResponse = new HotelOfferResponse();
        hotelOfferResponse.setData(List.of(hotelOfferData));

        return hotelOfferResponse;
    }

}
