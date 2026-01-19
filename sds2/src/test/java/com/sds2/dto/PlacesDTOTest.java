package com.sds2.dto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.price.PriceRange;

public class PlacesDTOTest {
    @Test
    public void testPlacesDTO(){
        PlacesDTO placesDTO = getExamplePlacesDTO();
    }
        
    @Test
    public void testSameObject(){
        PlacesDTO placesDTO1 = getExamplePlacesDTO();
        PlacesDTO placesDTO2 = placesDTO1;
        assertTrue(placesDTO1.equals(placesDTO2));
    }

    @Test
    public void testNullObject(){
        String nullObject = null;
        PlacesDTO placesDTO1 = getExamplePlacesDTO();
        assertTrue(!placesDTO1.equals(nullObject));
    }

    @Test
    public void testWrongClass(){
        String word = "hello";
        PlacesDTO placesDTO1 = getExamplePlacesDTO();
        assertTrue(!placesDTO1.equals(word));
    }


    @Test
    public void testSameValuesObject(){
        PlacesDTO placesDTO1 = getExamplePlacesDTO();
        PlacesDTO placesDTO2 = getExamplePlacesDTO();
        assertTrue(placesDTO1.equals(placesDTO2));
    }

    @Test
    public void testHashCode() {
        PlacesDTO placesDTO1 = getExamplePlacesDTO();
        int x = placesDTO1.hashCode();
    }
    

    public PlacesDTO getExamplePlacesDTO() {
        Long id = 12345L;
        String name = "lunaire";
        List<String> photoUrl = List.of("https://foxhole.wiki.gg/fr/wiki/Fichier:GrenadeLauncherCItemIcon.png");
        String type = "explosive";
        String address = "Kalokai";
        Location location = new Location(0.0, 0.0);
        Double rating = 10.0;
        PriceRange priceRange = new PriceRange(
        new PriceRange.Money("EUR", "10", 0),
        new PriceRange.Money("EUR", "20", 0)
        );
        String websiteUri = "https://foxhole.wiki.gg/fr/wiki/";
        PlacesDTO placesDTO = new PlacesDTO(id, name, photoUrl, type, address, location, rating, priceRange, websiteUri);
        return placesDTO;
    }
}
