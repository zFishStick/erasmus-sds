package com.sds2.response;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.sds2.classes.response.PlaceResponse;
import com.sds2.classes.response.PlaceResponse.AddressComponent;
import com.sds2.classes.response.PlaceResponse.DisplayName;
import com.sds2.classes.response.PlaceResponse.Photo;
import com.sds2.classes.response.PlaceResponse.PlacesData;

class PlaceResponseTest {

    @Test
    void testDefaultConstructorAndSettersGetters() {
        PlaceResponse resp = new PlaceResponse();
        assertNull(resp.getPlaces());
        assertNull(resp.getNextPageToken());
        assertNull(resp.getStatus());

        ArrayList<PlacesData> list = new java.util.ArrayList<>();
        PlacesData pd = new PlacesData();
        pd.setId("id123");
        pd.setName("Cafe");
        DisplayName dn = new DisplayName();
        dn.setText("Café Example");
        dn.setLanguageCode("en");
        pd.setDisplayName(dn);
        pd.setPrimaryType("food");
        pd.setFormattedAddress("123 Main St");
        Photo photo = new com.sds2.classes.response.PlaceResponse.Photo();
        photo.setHeight(100);
        photo.setWidth(200);
        photo.setName("front.jpg");
        pd.setPhotos(new Photo[] { photo });
        AddressComponent ac = new AddressComponent();
        ac.setLongText("Main Street");
        ac.setShortText("Main");
        ac.setTypes(new String[] { "route" });
        ac.setLanguageCode("en");
        pd.setAddressComponents(new AddressComponent[] { ac });
        pd.setRating(4.5);
        pd.setWebsiteUri("https://example.com");

        list.add(pd);
        resp.setPlaces(list);
        resp.setNextPageToken("token123");
        resp.setStatus("OK");

        assertNotNull(resp.getPlaces());
        assertEquals(1, resp.getPlaces().size());
        PlacesData got = resp.getPlaces().get(0);
        assertEquals("id123", got.getId());
        assertEquals("Cafe", got.getName());
        assertEquals("Café Example", got.getDisplayName().getText());
        assertEquals("en", got.getDisplayName().getLanguageCode());
        assertEquals("food", got.getPrimaryType());
        assertEquals("123 Main St", got.getFormattedAddress());
        assertNotNull(got.getPhotos());
        assertEquals(1, got.getPhotos().length);
        assertEquals(100, got.getPhotos()[0].getHeight());
        assertEquals("Main Street", got.getAddressComponents()[0].getLongText());
        assertEquals(Double.valueOf(4.5), got.getRating());
        assertEquals("token123", resp.getNextPageToken());
        assertEquals("OK", resp.getStatus());
    }

    @Test
    void testToStringContainsKeyFields() {
        com.sds2.classes.response.PlaceResponse.PlacesData pd = new com.sds2.classes.response.PlaceResponse.PlacesData();
        pd.setId("x");
        pd.setName("Y");
        com.sds2.classes.response.PlaceResponse.DisplayName dn = new com.sds2.classes.response.PlaceResponse.DisplayName();
        dn.setText("Display");
        pd.setDisplayName(dn);

        String s = pd.toString();
        assertTrue(s.contains("x"), "toString should contain id");
        assertTrue(s.contains("Y"), "toString should contain name");
        assertTrue(s.contains("Display"), "toString should contain display text");
    }

    @Test
    void testEmptyListAndNullFields() {
        com.sds2.classes.response.PlaceResponse resp = new com.sds2.classes.response.PlaceResponse();
        resp.setPlaces(new java.util.ArrayList<com.sds2.classes.response.PlaceResponse.PlacesData>());
        assertNotNull(resp.getPlaces());
        assertTrue(resp.getPlaces().isEmpty());

        com.sds2.classes.response.PlaceResponse.PlacesData pd = new com.sds2.classes.response.PlaceResponse.PlacesData();
        // Leave nested fields null and verify getters return null
        assertNull(pd.getDisplayName());
        assertNull(pd.getAddressComponents());
        assertNull(pd.getPhotos());
        assertNull(pd.getLocation());
        assertNull(pd.getPriceRange());
    }

    @Test
    void testPhotoAndAddressComponentMutability() {
        com.sds2.classes.response.PlaceResponse.Photo p = new com.sds2.classes.response.PlaceResponse.Photo();
        p.setHeight(10);
        p.setWidth(20);
        p.setName("n");
        assertEquals(10, p.getHeight());
        assertEquals(20, p.getWidth());
        assertEquals("n", p.getName());

        com.sds2.classes.response.PlaceResponse.AddressComponent ac = new com.sds2.classes.response.PlaceResponse.AddressComponent();
        ac.setLongText("L");
        ac.setShortText("S");
        ac.setTypes(new String[] { "t1", "t2" });
        ac.setLanguageCode("lc");
        assertEquals("L", ac.getLongText());
        assertEquals("S", ac.getShortText());
        assertArrayEquals(new String[] { "t1", "t2" }, ac.getTypes());
        assertEquals("lc", ac.getLanguageCode());
    }

}