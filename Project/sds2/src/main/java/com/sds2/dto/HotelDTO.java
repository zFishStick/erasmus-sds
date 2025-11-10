package com.sds2.dto;

import java.io.Serializable;

import com.sds2.classes.response.HotelResponse.Address;

public record HotelDTO (
    String name,
    String cityName,
    String countryCode,
    Address address
) implements Serializable {}
