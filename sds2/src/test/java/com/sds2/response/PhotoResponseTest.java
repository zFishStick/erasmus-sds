package com.sds2.response;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.sds2.classes.response.PhotoResponse;

class PhotoResponseTest {

    @Test
    void testGettersAndSetters() {
        PhotoResponse resp = new PhotoResponse();
        resp.setName("Sample Photo");
        resp.setPhotoUri("http://example.com/photo.jpg");

        assertEquals("Sample Photo", resp.getName());
        assertEquals("http://example.com/photo.jpg", resp.getPhotoUri());
    }
    
}
