package com.sds2.classes.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class WaypointRequest {
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
}
