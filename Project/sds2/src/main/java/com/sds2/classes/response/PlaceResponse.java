package com.sds2.classes.response;

import java.util.List;

import com.sds2.classes.Location;
import com.sds2.classes.price.PriceRange;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlaceResponse {

    private List<PlacesData> places;
    private String nextPageToken;
    private String status;

    public List<PlacesData> getPlaces() { return places; }
    public void setPlaces(List<PlacesData> places) { this.places = places; }

    public String getNextPageToken() { return nextPageToken; }
    public void setNextPageToken(String nextPageToken) { this.nextPageToken = nextPageToken; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class PlacesData {
        private String id;
        private String name;
        private DisplayName displayName;
        private String primaryType;
        private String formattedAddress;
        private Location location;
        private Double rating;
        private Photo[] photos;
        private PriceRange priceRange;
    }

    public static class DisplayName {
        private String text;
        private String languageCode;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getLanguageCode() {
            return languageCode;
        }

        public void setLanguageCode(String languageCode) {
            this.languageCode = languageCode;
        }
    }

    public static class Photo {
        private int height;
        private int width;
        private String name;

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}