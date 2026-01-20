package com.sds2.enums;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.sds2.classes.enums.UserStatusResponse;

class UserStatusResponseTest {

    @Test
    void getMessage_returnsExpectedTexts() {
        assertEquals("Account created successfully", UserStatusResponse.SUCCESS.getMessage());
        assertEquals("User with this email already exists", UserStatusResponse.USER_ALREADY_EXISTS.getMessage());
        assertEquals("Account creation failed", UserStatusResponse.FAILURE.getMessage());
    }

    @Test
    void values_containsAllConstantsInDeclaredOrder() {
        UserStatusResponse[] vals = UserStatusResponse.values();
        assertEquals(3, vals.length);
        assertEquals(UserStatusResponse.SUCCESS, vals[0]);
        assertEquals(UserStatusResponse.USER_ALREADY_EXISTS, vals[1]);
        assertEquals(UserStatusResponse.FAILURE, vals[2]);
    }

    @Test
    void valueOf_returnsConstantForValidName_andThrowsForInvalid() {
        assertEquals(UserStatusResponse.SUCCESS, UserStatusResponse.valueOf("SUCCESS"));
        assertEquals(UserStatusResponse.USER_ALREADY_EXISTS, UserStatusResponse.valueOf("USER_ALREADY_EXISTS"));
        assertEquals(UserStatusResponse.FAILURE, UserStatusResponse.valueOf("FAILURE"));
        assertThrows(IllegalArgumentException.class, () -> UserStatusResponse.valueOf("UNKNOWN_STATUS"));
    }

    @Test
    void getMessage_notNullAndNotEmpty_forAllConstants() {
        for (UserStatusResponse s : UserStatusResponse.values()) {
            String msg = s.getMessage();
            assertNotNull(msg);
            assertFalse(msg.trim().isEmpty());
        }
    }

    @Test
    void toString_equalsName_forAllConstants() {
        for (UserStatusResponse s : UserStatusResponse.values()) {
            assertEquals(s.name(), s.toString());
        }
    }
}