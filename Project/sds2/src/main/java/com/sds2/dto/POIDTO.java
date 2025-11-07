package com.sds2.dto;

import com.sds2.classes.Price;

public record POIDTO
(
    String name, 
    String description, 
    String type,
    Price price,
    String pictures,
    String minimumDuration,
    String bookingLink
){}