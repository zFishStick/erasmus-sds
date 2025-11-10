package com.sds2.classes.response;

import java.util.List;

import com.sds2.classes.Price;
import com.sds2.classes.Room;

public class HotelOfferResponse {

    private List<HotelOfferData> data;

    public List<HotelOfferData> getData() { return data; }

    public void setData(List<HotelOfferData> data) { this.data = data; }
    
    public static class HotelOfferData {
        private Offers offers;
        private int adults;
        private Price price;

        public Offers getOffers() { return offers; }

        public void setOffers(Offers offers) { this.offers = offers; }

        public int getAdults() { return adults; }

        public void setAdults(int adults) { this.adults = adults; }

        public String getId() { return offers != null ? offers.getId() : null; }

        public String getCheckInDate() { return offers != null ? offers.getCheckInDate() : null; }

        public String getCheckOutDate() { return offers != null ? offers.getCheckOutDate() : null; }

        public Room getRoom() { return offers != null ? offers.getRoom() : null; }

        public Price getPrice() { return price; }
    }

    public static class Offers {
        private String id;
        private String checkInDate;
        private String checkOutDate;
        private Room room;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getCheckInDate() { return checkInDate; }
        public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }

        public String getCheckOutDate() { return checkOutDate; }
        public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }

        public Room getRoom() { return room; }
        public void setRoom(Room room) { this.room = room; }

        @Override
        public String toString() {
            return "Offers{id=" + id + ", checkInDate='" + checkInDate + "', checkOutDate='" + checkOutDate + "', room=" + room + "}";
        }
    }
    
}

// "offers":[{"id":"N8D7VAMA3W","checkInDate":"2025-11-11","checkOutDate":"2025-11-12",
// "rateCode":"PRO","rateFamilyEstimated":{"code":"PRO","type":"P"},
// "commission":{"percentage":"4.00"},"boardType":"BREAKFAST",
// "room":{"type":"ROH","typeEstimated":{"category":"STANDARD_ROOM"},
// "description":{"text":"HRS-Rate - LastMinute-Discount\nStandard room A standard room consists of a room with shower-toilet or bathtub-toilet.","lang":"EN"}},
