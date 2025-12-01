package com.sds2.classes.enums;

public enum HotelEnum {
    HOTELS_DATA("hotels"),
    SEARCH_CONTEXT("hotelSearchContext"),
    CURRENT_PAGE("currentPage"),
    TOTAL_PAGES("totalPages"),
    PAGE_SIZE("pageSize");

    private final String value;

    HotelEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
}