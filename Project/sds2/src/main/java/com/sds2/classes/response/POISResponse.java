package com.sds2.classes.response;

import java.util.List;

import com.sds2.classes.GeoCode;
import com.sds2.classes.Price;

public class POISResponse {
    
    private List<POIData> data;

    public List<POIData> getData() { return data; }

    public void setData(List<POIData> data) { this.data = data; }

    public static class POIData {
        private String name;
        private String description;
        private String type;
        private Price price;
        private List<String> pictures;
        private String minimumDuration;
        private String bookingLink;
        private GeoCode geoCode;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Price getPrice() { return price; }
        public void setPrice(Price price) { this.price = price; }

        public List<String> getPictures() { return pictures; }
        public void setPictures(List<String> pictures) { this.pictures = pictures; }

        public String getMinimumDuration() { return minimumDuration; }
        public void setMinimumDuration(String minimumDuration) { this.minimumDuration = minimumDuration; }

        public String getBookingLink() { return bookingLink; }
        public void setBookingLink(String bookingLink) { this.bookingLink = bookingLink; }

        public GeoCode getGeoCode() { return geoCode; }
        public void setGeoCode(GeoCode geoCode) { this.geoCode = geoCode; }
    }

}

