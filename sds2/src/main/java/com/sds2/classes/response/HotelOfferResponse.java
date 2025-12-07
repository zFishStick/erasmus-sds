package com.sds2.classes.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class HotelOfferResponse {

    private List<HotelOfferData> data;

    public List<HotelOfferData> getData() { return data; }

    public void setData(List<HotelOfferData> data) { this.data = data; }
    
    public static class HotelOfferData {
        private List<OfferItem> offers;

        public List<OfferItem> getOffers() { return offers; }

        public void setOffers(List<OfferItem> offers) { this.offers = offers; }
    }

    @Getter @Setter
    public static class OfferItem {
        private String id;
        private String checkInDate;
        private String checkOutDate;
        private OfferGuests guests;
        private OfferPrice price;
        private OfferRoom room;

    }

    @Getter @Setter
    public static class OfferGuests {
        private Integer adults;
    }
    
    @Getter @Setter
    public static class OfferPrice {
        private String currency;
        private String total;
    }

    @Getter @Setter
    public static class OfferRoom {
        private String type;
        private OfferRoomTypeEstimated typeEstimated;
        private OfferRoomDescription description;
    }

    @Getter @Setter
    public static class OfferRoomTypeEstimated {
        private String category;
    }

    @Getter @Setter
    public static class OfferRoomDescription {
        private String text;
        private String lang;
    }
}
