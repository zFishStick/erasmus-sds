package com.sds2.classes.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class POIRequest implements Serializable {
    private String destination;
    private String countryCode;
    private Double latitude;
    private Double longitude;
    private String startDate;
    private String endDate;
}
