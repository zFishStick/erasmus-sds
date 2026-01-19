package com.sds2.service;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sds2.classes.City;
import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.response.CityResponse;
import com.sds2.classes.response.CityResponse.Address;
import com.sds2.classes.response.CityResponse.CityData;
import com.sds2.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {
    @Mock
    CityRepository cityRepository;

    @Mock
    AmadeusAPICall amadeusAPICall;

    @InjectMocks
    private CityService cityService;

    @Test
    void testGetCity() throws IOException {
        CityData cityData = new CityData("exParis", new Address("countryCode"), new GeoCode(1L, 1L), "iataCode");
        City city = new City("exParis", "country", 1L, 1L);

        CityResponse exampleCityResponse = new CityResponse(List.of(cityData));

        when(amadeusAPICall.getAPIResponse(any(), any())).thenReturn(exampleCityResponse);
        when(cityRepository.save(any())).thenReturn(city);
        cityService.getCity("exParis");
    }
}
