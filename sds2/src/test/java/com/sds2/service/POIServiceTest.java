package com.sds2.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sds2.WebClientMockedTestClasse;
import com.sds2.classes.Price;
import com.sds2.classes.coordinates.GeoCode;
import com.sds2.classes.response.POISResponse;
import com.sds2.classes.response.POISResponse.POIData;
import com.sds2.dto.POIDTO;
import com.sds2.repository.POIRepository;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class POIServiceTest extends WebClientMockedTestClasse{
    @Mock
    private POIRepository poiRepository;

    @InjectMocks
    private POIService poiservice;

    @Test
    void testGetPointOfInterests() {
        POISResponse examplePOISReponse = getExamplePOISResponse();
        when(responseSpec.bodyToMono(POISResponse.class)).thenReturn(Mono.just(examplePOISReponse));

        GeoCode geocode = new GeoCode(0D, 0D);
        List<POIDTO> result = poiservice.getPointOfInterests(geocode, "Paris", "FR");
    }

    POISResponse getExamplePOISResponse(){
        POIData poiData = new POIData();

        poiData.setName("hello");
        poiData.setDescription("hello");
        poiData.setType("hello");
        poiData.setPrice(new Price(0, "PLN"));
        poiData.setPictures(List.of("hello"));
        poiData.setMinimumDuration("hello");
        poiData.setBookingLink("hello");
        poiData.setGeoCode(new GeoCode(0L, 0L));

        POISResponse poisResponse = new POISResponse();
        poisResponse.setData(List.of(poiData));
        return poisResponse;
    }
}