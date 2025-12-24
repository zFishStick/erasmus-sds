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
        SUCCESS("Login successful"),
        USER_NOT_FOUND("User not found"),
        INVALID_CREDENTIALS("Invalid credentials");

        private final String description;

        LoginStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
    
}
