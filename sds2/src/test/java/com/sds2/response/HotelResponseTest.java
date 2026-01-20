package com.sds2.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.response.HotelResponse;
import com.sds2.classes.response.HotelResponse.HotelData;
import com.sds2.classes.response.HotelResponse.Address;


class HotelResponseTest {

    @Test
    void testDataGetterSetter() {
        HotelResponse resp = new HotelResponse();
        HotelData hd = new HotelData();
        List<HotelData> list = Arrays.asList(hd);

        resp.setData(list);
        assertSame(list, resp.getData());
        assertSame(hd, resp.getData().get(0));
    }

    @Test
    void testHotelDataPropertiesAndNestedObjects() {
        HotelData hd = new HotelData();
        hd.setName("Hotel A");
        hd.setHotelId("H123");
        hd.setIataCode("IAT");

        GeoCode geo = new GeoCode();
        hd.setGeoCode(geo);

        Address addr = new Address();
        addr.setCountryCode("US");
        addr.setCityName("NYC");
        List<String> lines = Arrays.asList("123 Main St", "Suite 100");
        addr.setLines(lines);
        hd.setAddress(addr);

        HotelResponse resp = new HotelResponse();
        resp.setData(Arrays.asList(hd));

        HotelData actual = resp.getData().get(0);
        assertEquals("Hotel A", actual.getName());
        assertEquals("H123", actual.getHotelId());
        assertEquals("IAT", actual.getIataCode());
        assertSame(geo, actual.getGeoCode());
        assertSame(addr, actual.getAddress());
        assertEquals("US", actual.getAddress().getCountryCode());
        assertEquals("NYC", actual.getAddress().getCityName());
        assertSame(lines, actual.getAddress().getLines());
        assertEquals("123 Main St", actual.getAddress().getLines().get(0));
    }

    @Test
    void testAddressGetterSetter() {
        Address addr = new Address();
        addr.setCountryCode("BR");
        addr.setCityName("Rio");
        List<String> l = Arrays.asList("Rua A", "Apto 1");
        addr.setLines(l);

        assertEquals("BR", addr.getCountryCode());
        assertEquals("Rio", addr.getCityName());
        assertSame(l, addr.getLines());
    }
}