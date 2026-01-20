package com.sds2.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sds2.classes.response.CityResponse;

class CityResponseTest {

    @Test
    void testGettersAndSetters() {
        CityResponse.Address address = new CityResponse.Address();
        address.setCountryCode("US");
        assertEquals("US", address.getCountryCode());

        CityResponse.CityData city = new CityResponse.CityData();
        city.setName("Test City");
        city.setAddress(address);
        city.setGeoCode(null);
        city.setIataCode("TST");

        assertEquals("Test City", city.getName());
        assertSame(address, city.getAddress());
        assertNull(city.getGeoCode());
        assertEquals("TST", city.getIataCode());

        CityResponse response = new CityResponse();
        List<CityResponse.CityData> list = new ArrayList<>();
        list.add(city);
        response.setData(list);

        assertNotNull(response.getData());
        assertEquals(1, response.getData().size());
        assertSame(city, response.getData().get(0));
    }

    @Test
    void testEmptyDataList() {
        CityResponse response = new CityResponse();
        assertNull(response.getData());
        response.setData(Collections.emptyList());
        assertTrue(response.getData().isEmpty());
    }
}
