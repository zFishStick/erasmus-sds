package com.sds2.classes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PhotosTest {

    @Test
    void createPhotosInstance() {

        Photos photos = new Photos();
        photos.setName("SamplePhoto");

        assertEquals("SamplePhoto", photos.getName());
    }
    
}
