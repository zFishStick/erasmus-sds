package com.sds2.classes.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class WaypointRequest {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String destination;
    private String country;
    private Long userId;
}