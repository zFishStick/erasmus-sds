package com.sds2.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.sds2.classes.response.LoginResponse;

class LoginResponseTest {
    
    @Test
    void testLoginResponseCreation() {
        LoginResponse resp = new LoginResponse(true, LoginResponse.LoginStatus.SUCCESS, "http://redirect.url");
        
        assertEquals(true, resp.isSuccess());
        assertEquals(LoginResponse.LoginStatus.SUCCESS, resp.getMessage());
        assertEquals("http://redirect.url", resp.getRedirectUrl());
    }

}
