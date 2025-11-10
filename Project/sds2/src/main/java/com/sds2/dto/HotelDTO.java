package com.sds2.dto;

import java.io.Serializable;
import java.util.List;

public record HotelDTO (
    String name,
    String cityName,
    String countryCode,
    List<String> lines

) implements Serializable {}
