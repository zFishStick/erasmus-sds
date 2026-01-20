package com.sds2.classes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class RoomTest {
    
    @Test
    void testRoomGettersAndSetters() {
        Room room = new Room();
        room.setCategory("Deluxe");
        room.setDescription("A deluxe room with sea view.");

        assertEquals("Deluxe", room.getCategory());
        assertEquals("A deluxe room with sea view.", room.getDescription());
    }

}
