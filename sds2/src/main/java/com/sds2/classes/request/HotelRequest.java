package com.sds2.classes.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelRequest {
    private String destination;
    private String countryCode;
    private Double latitude;
    private Double longitude;
    private String checkInDate;
    private String checkOutDate;
}