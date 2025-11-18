package com.sds2.classes.response;

import java.util.List;

public class HotelOfferResponse {

    private List<HotelOfferData> data;

    public List<HotelOfferData> getData() { return data; }

    public void setData(List<HotelOfferData> data) { this.data = data; }
    
    public static class HotelOfferData {
        private List<OfferItem> offers;

        public List<OfferItem> getOffers() { return offers; }

        public void setOffers(List<OfferItem> offers) { this.offers = offers; }
    }

    public static class OfferItem {
        private String id;
        private String checkInDate;
        private String checkOutDate;
        private OfferGuests guests;
        private OfferPrice price;
        private OfferRoom room;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getCheckInDate() { return checkInDate; }
        public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

        public String getCheckOutDate() { return checkOutDate; }
        public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

        public OfferGuests getGuests() { return guests; }
        public void setGuests(OfferGuests guests) { this.guests = guests; }

        public OfferPrice getPrice() { return price; }
        public void setPrice(OfferPrice price) { this.price = price; }

        public OfferRoom getRoom() { return room; }
        public void setRoom(OfferRoom room) { this.room = room; }
    }

    public static class OfferGuests {
        private Integer adults;

        public Integer getAdults() { return adults; }
        public void setAdults(Integer adults) { this.adults = adults; }
    }

    public static class OfferPrice {
        private String currency;
        private String total;

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public String getTotal() { return total; }
        public void setTotal(String total) { this.total = total; }
    }

    public static class OfferRoom {
        private String type;
        private OfferRoomTypeEstimated typeEstimated;
        private OfferRoomDescription description;

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public OfferRoomTypeEstimated getTypeEstimated() { return typeEstimated; }
        public void setTypeEstimated(OfferRoomTypeEstimated typeEstimated) { this.typeEstimated = typeEstimated; }

        public OfferRoomDescription getDescription() { return description; }
        public void setDescription(OfferRoomDescription description) { this.description = description; }
    }

    public static class OfferRoomTypeEstimated {
        private String category;

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    public static class OfferRoomDescription {
        private String text;
        private String lang;

        public String getText() { return text; }
        public void setText(String text) { this.text = text; }

        public String getLang() { return lang; }
        public void setLang(String lang) { this.lang = lang; }
    }
}
