package com.sds2.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.sds2.classes.Location;
import com.sds2.classes.Photos;
import com.sds2.classes.response.PlaceResponse;
import com.sds2.dto.PlacesDTO;

@SpringBootTest
class PlaceServiceTest {

    @Autowired
    private PlaceService placeService;

    @Test
    void findPlaceFromTextTest() {
        String place = "Poznan,%20Poland";
        PlaceResponse response = placeService.searchText(place);
        assertNotNull(response);
        System.out.println("Result " + response.getPlaces()); // place_id: ChIJtwrh7NJEBEcR0b80A5gx6qQ
    }

    // @Test
    // void getPhotoPlaceTest() {
    //     String photoReference = "AWn5SU7AAuohT-9tyILcWPsvuGYb46wtK1-D0I1wXeHdZXe4OSeIvDZ9QZQn5cCGHzQE8PKW8zq955KUAtNVwBr6LdPOfp4fs-lFD3yoWIm6wL9wAhNov8nAwJ8ofkq1lulZaS0sm_uM-vV2dLLhyJ5eitkx99WgwT3e5lbpewCIaAOX3WqcCqpBHf3MOK4wi46iX1n55269P-g4RcYLm-UDsAJAXf_rFJuXvk52FR2ah6y4NTqxE4BOGf45w5vgS0TNfvudAZKriitnWiNV40_BjaCSqeIWPjpsvECkFKsTsifxLvyxNxER5gzoMlgGUZrPXGWl-umW0nNHp3ror9TWqblm86g0ydzUyKeo7PJ_C5NVa053AkIwwaWy2F6inl8OT94qtuY2_14KWHUKUjQHwFp3DV10OLYs_fJ2Jy9sx6Wk0w";
    //     String placeId = "ChIJtwrh7NJEBEcR0b80A5gx6qQ"; // Poznan, Poland
    //     // photoName = places/PLACE_ID/photos/PHOTO_REFERENCE
        
    //     String photoName = "places/" + placeId + "/photos/" + photoReference;

    //     List<String> photoUrls = placeService.getPlacePhoto(new Photos[] { new Photos(photoName) });

    //     assertNotNull(photoUrls);
    //     System.out.println(photoUrls);
    // }

    @Test
    void searchNearbyTest() {
        double latitude = 52.405678599999995;
        double longitude = 16.9312766;

        Location location = new Location(latitude, longitude);

        List<PlacesDTO> response = placeService.searchNearby(location, "Poznan", "Poland");
        assertNotNull(response);
        System.out.println(response);
    }
}
