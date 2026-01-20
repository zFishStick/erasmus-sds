package com.sds2.dto;

import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Constructor;
import org.junit.jupiter.api.Test;
import com.sds2.classes.coordinates.Location;
import com.sds2.classes.entity.Waypoint;


class WaypointDTOTest {

    @Test
    void fromEntity_mapsAllFields_whenValuesPresent() throws Exception {
        Waypoint w = new Waypoint();
        w.setId(42L);
        w.setVia(true);
        w.setName("WP");
        // try to construct Location reflectively if available; otherwise leave null
        Location loc = null;
        try {
            Constructor<Location> ctor = Location.class.getDeclaredConstructor();
            ctor.setAccessible(true);
            loc = ctor.newInstance();
        } catch (NoSuchMethodException _) {
            // Location has no no-arg ctor; leave as null
        }
        w.setLocation(loc);
        w.setAddress("Addr");
        w.setDestination("Dest");
        w.setCountry("Land");

        WaypointDTO dto = WaypointDTO.fromEntity(w);

        assertNotNull(dto);
        assertEquals(42L, dto.id());
        assertTrue(dto.via());
        assertEquals("WP", dto.name());
        if (loc != null) {
            assertSame(loc, dto.location());
        } else {
            assertNull(dto.location());
        }
        assertEquals("Addr", dto.address());
        assertEquals("Dest", dto.destination());
        assertEquals("Land", dto.country());
    }

    @Test
    void fromEntity_handlesNullFields_andNullId() {
        Waypoint w = new Waypoint();
        // leave all fields null / default
        w.setVia(false);

        WaypointDTO dto = WaypointDTO.fromEntity(w);

        assertNotNull(dto);
        assertNull(dto.id());
        assertFalse(dto.via());
        assertNull(dto.name());
        assertNull(dto.location());
        assertNull(dto.address());
        assertNull(dto.destination());
        assertNull(dto.country());
    }

    @Test
    void fromEntity_returnsDistinctDtos_butEqualWhenValuesMatch() {
        Waypoint a = new Waypoint();
        a.setId(7L);
        a.setVia(false);
        a.setName("Same");
        a.setAddress("A");
        a.setDestination("B");
        a.setCountry("C");

        Waypoint b = new Waypoint();
        b.setId(7L);
        b.setVia(false);
        b.setName("Same");
        b.setAddress("A");
        b.setDestination("B");
        b.setCountry("C");

        WaypointDTO da = WaypointDTO.fromEntity(a);
        WaypointDTO db = WaypointDTO.fromEntity(b);

        assertNotSame(da, db);
        assertEquals(da, db);
        assertEquals(da.hashCode(), db.hashCode());
    }

    @Test
    void fromEntity_throwsWhenInputIsNull() {
        assertThrows(NullPointerException.class, () -> WaypointDTO.fromEntity(null));
    }
}