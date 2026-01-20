package com.sds2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.sds2.repository.POIRepository;

import com.sds2.classes.poi.POIInfo;
import com.sds2.classes.entity.POI;
import com.sds2.classes.Price;
import com.sds2.classes.coordinates.GeoCode;
import com.sds2.dto.POIDTO;

class POIServiceTest {
    POIInfo info;
    POI poi;

    @BeforeEach
    void setup() {
        info = new POIInfo(
            "PlaceName", "TypeA", "A nice place", 
            "pic1", "45", "bookLink"
        );

        Price price = new Price(42.0, "USD");
        GeoCode coords = new GeoCode(10.0, 20.0);

        poi = new POI("Metropolis", "MP", info, price, coords);
    }

    @Test
    void testAddPOI_nullThrows() {
        POIRepository repo = (POIRepository)
            Proxy.newProxyInstance(
                POIRepository.class.getClassLoader(),
                new Class<?>[] { POIRepository.class },
                (proxy, method, args) -> null
            );

        POIService svc = new POIService(repo, null, null);

        try {
            svc.addPOI(null);
            fail("Expected IllegalArgumentException for null POI");
        } catch (IllegalArgumentException e) {
            assertEquals("POI cannot be null", e.getMessage());
        }
    }

    @Test
    void testAddPOI_savesToRepository() {
        final Object[] saved = new Object[1];

        POIRepository repo = (POIRepository)
            Proxy.newProxyInstance(
                POIRepository.class.getClassLoader(),
                new Class<?>[] { POIRepository.class },
                (proxy, method, args) -> {
                    if ("save".equals(method.getName()) && args != null && args.length == 1) {
                        saved[0] = args[0];
                        return null;
                    }
                    return defaultReturn(method.getReturnType());
                }
            );
        
        POIService svc = new POIService(repo, null, null);
        svc.addPOI(poi);

        assertSame(poi, saved[0]);
    }

    @Test
    void testGetPOIById_returnsFromRepository() {

        final POI expected = poi;

        POIRepository repo = (POIRepository)
            Proxy.newProxyInstance(
                POIRepository.class.getClassLoader(),
                new Class<?>[] { POIRepository.class },
                (proxy, method, args) -> {
                    if ("findById".equals(method.getName()) && args != null && args.length == 1) {
                        return expected;
                    }
                    return defaultReturn(method.getReturnType());
                }
            );

        POIService svc = new POIService(repo, null, null);
        POI actual = svc.getPOIById(123L);

        assertSame(expected, actual);
    }

    @Test
    void testGetPointOfInterests_returnsFromRepository() {




        POIRepository repo = (POIRepository)
            Proxy.newProxyInstance(
                POIRepository.class.getClassLoader(),
                new Class<?>[] { POIRepository.class },
                (proxy, method, args) -> {
                    if ("findByCityNameAndCountryCode".equals(method.getName()) && args != null && args.length == 2) {
                        return Arrays.asList(poi);
                    }
                    return defaultReturn(method.getReturnType());
                }
            );

        POIService svc = new POIService(repo, null, null);

        GeoCode coords = null;
        List<POIDTO> dtos = svc.getPointOfInterests(coords, "Metropolis", "MP");

        assertEquals(1, dtos.size());
        POIDTO dto = dtos.get(0);
        assertEquals("Metropolis", dto.cityName());
        assertEquals("PlaceName", dto.name());
        assertEquals("A nice place", dto.description());
        assertEquals("TypeA", dto.type());
        assertEquals(42.0, dto.price().getAmount(), 0.0001);
        assertEquals("pic1", dto.pictures());
        assertEquals("45", dto.minimumDuration());
        assertEquals("bookLink", dto.bookingLink());
    }

    // helper to provide sensible defaults for proxy methods
    private static Object defaultReturn(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            if (List.class.isAssignableFrom(returnType)) {
                return Collections.emptyList();
            }
            return null;
        }
        if (returnType == boolean.class) return false;
        if (returnType == byte.class) return (byte)0;
        if (returnType == short.class) return (short)0;
        if (returnType == int.class) return 0;
        if (returnType == long.class) return 0L;
        if (returnType == float.class) return 0f;
        if (returnType == double.class) return 0d;
        if (returnType == char.class) return '\0';
        return null;
    }
}