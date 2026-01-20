package com.sds2.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ChatActivityDTOTest {
    @Test
    void testGettersAndSetters() {
        ChatActivityDTO dto = new ChatActivityDTO(
            "sourceValue",
            "nameValue",
            "descriptionValue",
            "typeValue",
            "pictureValue",
            "minimumDurationValue",
            "bookingLinkValue",
            99.99,
            "USD",
            "addressValue",
            4.5,
            "http://website.uri",
            12.345678,
            98.765432
        );

        assertEquals("sourceValue", dto.source());
        assertEquals("nameValue", dto.name());
        assertEquals("descriptionValue", dto.description());
        assertEquals("typeValue", dto.type());
        assertEquals("pictureValue", dto.picture());
        assertEquals("minimumDurationValue", dto.minimumDuration());
        assertEquals("bookingLinkValue", dto.bookingLink());
        assertEquals(99.99, dto.priceAmount());
        assertEquals("USD", dto.priceCurrency());
        assertEquals("addressValue", dto.address());
        assertEquals(4.5, dto.rating());
        assertEquals("http://website.uri", dto.websiteUri());
        assertEquals(12.345678, dto.latitude());
        assertEquals(98.765432, dto.longitude());
    }
}
