package com.sds2.classes.enums;

public enum GoogleBodyEnum {
    CONTENTTYPE("Content-Type"),
    APPLICATIONJSON("application/json; charset=UTF-8"),
    X_GOOG_API_KEY("X-Goog-Api-Key"),
    X_GOOG_FIELD_MASK("X-Goog-FieldMask");

    private final String value;

    GoogleBodyEnum(String value) {
        this.value = value;
    }

     public String getValue() {
        return value;
    }

}