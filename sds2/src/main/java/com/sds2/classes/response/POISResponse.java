package com.sds2.classes.response;

import java.util.List;

import com.sds2.classes.Price;
import com.sds2.classes.coordinates.GeoCode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class POISResponse {
    
    private List<POIData> data;

    @Getter @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class POIData {
        private String name;
        private String description;
        private String type;
        private Price price;
        private List<String> pictures;
        private String minimumDuration;
        private String bookingLink;
        private GeoCode geoCode;
    }

}

