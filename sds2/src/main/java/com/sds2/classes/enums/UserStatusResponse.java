package com.sds2.classes.enums;

public enum UserStatusResponse {
    SUCCESS("Account created successfully"),
    USER_ALREADY_EXISTS("User with this email already exists"),
    FAILURE("Account creation failed");

    private final String message;

    UserStatusResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}