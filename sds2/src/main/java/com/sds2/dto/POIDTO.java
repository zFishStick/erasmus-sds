package com.sds2.dto;

import java.io.Serializable;

import com.sds2.classes.Price;
import com.sds2.classes.coordinates.GeoCode;

public record POIDTO
(
    String cityName,
    String name, 
    String description, 
    String type,
    Price price,
    String pictures,
    String minimumDuration,
    String bookingLink,
    GeoCode coordinates
    
) implements Serializable{}
