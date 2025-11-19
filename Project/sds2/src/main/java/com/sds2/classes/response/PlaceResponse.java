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
        private String websiteUri;
    }

    @Getter @Setter
    public static class DisplayName {
        private String text;
        private String languageCode;
    }

    @Getter @Setter
    public static class Photo {
        private int height;
        private int width;
        private String name;
    }
}