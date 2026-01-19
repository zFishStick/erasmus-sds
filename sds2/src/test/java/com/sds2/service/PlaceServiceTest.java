package com.sds2.service;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import com.sds2.classes.coordinates.Location;
import com.sds2.classes.price.PriceRange;
import com.sds2.classes.price.PriceRange.Money;
import com.sds2.classes.response.PhotoResponse;
import com.sds2.classes.response.PlaceResponse;
import com.sds2.classes.response.PlaceResponse.AddressComponent;
import com.sds2.classes.response.PlaceResponse.DisplayName;
import com.sds2.classes.response.PlaceResponse.Photo;
import com.sds2.classes.response.PlaceResponse.PlacesData;
import com.sds2.dto.PlacesDTO;
import com.sds2.repository.PlacesRepository;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock
    private PlacesRepository placesRepository;

    @Mock
    private GoogleAuthService googleAuthService;

    @Mock
    private GoogleAPIPlaceRequestService googleAPIPlaceRequest;

    @Mock
    private WebClient.Builder webClientBuilder;

    @InjectMocks
    private PlaceService placeService;
    
    PlacesData getExamplePlaceResponse ()
    {DisplayName displayName = new DisplayName("text", "language code");

        AddressComponent[] addressComponents = {
                new AddressComponent(
                "country", 
                "short text", 
                new String[] {"country"}, 
                "language code"
                ), 
                new AddressComponent(
                "city", 
                "short text", 
                new String[] {"locality"}, 
                "language code"
                )
            };
        
        Location location = new Location(0D, 0D);

        Photo[] photos = {new Photo(1, 1, "name")};

        Money startPrice = new Money("currencyCode", "units", 0);
        Money endPrice = new Money("currencyCode", "units", 1);
        
        PriceRange priceRange = new PriceRange(startPrice, endPrice);
        return new PlacesData(
            "id", 
            "name", 
            displayName, 
            "primary type", 
            "formattedAddress", 
            addressComponents, 
            location, 
            0D,
            photos,
            priceRange, 
            "websiteUri"
        );
        
    }


    // @Test
    // void testSearchNearby() {
    //     PlacesData placesData = getExamplePlaceResponse();

    //     Mockito.when(googleAPIPlaceRequest.getPlaceResponse(any(), any(), any())).thenReturn(new PlaceResponse(List.of(placesData), "nextPageToken", "status"));
    //     Mockito.when(googleAPIPlaceRequest.getPhotoResponse(any())).thenReturn(new PhotoResponse("name", "uri"));

    //     placeService.searchNearby(new Location(0D, 0D), "city", "country");
    // }

    // @Test
    // void addOtherPlaces() {
    //     String city = "Rome";
    //     String country = "Italy";
    //     double latitude = 41.89193;
    //     double longitude = 12.51133;

    //     Location location = new Location(latitude, longitude);

    //     List<PlacesDTO> response = placeService.searchNearby(location, city, country);
    //     assertNotNull(response);
    //     System.out.println(response);   
    // }
}
