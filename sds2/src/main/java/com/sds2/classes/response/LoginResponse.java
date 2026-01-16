package com.sds2.classes.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class LoginResponse {
    private boolean success;
    private LoginStatus message;
    private String redirectUrl;

    public enum LoginStatus {
        SUCCESS,
        USER_NOT_FOUND,
        INVALID_CREDENTIALS
    }
}


