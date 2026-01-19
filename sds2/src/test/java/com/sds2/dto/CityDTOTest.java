package com.sds2.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class CityDTOTest {
    @Test
    public void testCityDTO(){
        CityDTO cityDTO = getExampleCityDTO();
    }

    @Test
    public void testSameObject(){
        CityDTO cityDTO1 = getExampleCityDTO();
        CityDTO cityDTO2 = cityDTO1;
        assertTrue(cityDTO1.equals(cityDTO2));
    }

    @Test
    public void testSameValuesObject(){
        CityDTO cityDTO1 = getExampleCityDTO();
        CityDTO cityDTO2 = getExampleCityDTO();
        assertTrue(cityDTO1.equals(cityDTO2));
    }

    @Test
    public void testdifferentValuesObject(){
        CityDTO cityDTO1 = getExampleCityDTO();
        CityDTO cityDTO2 = getDifferentExampleCityDTO();
        assertTrue(!cityDTO1.equals(cityDTO2));
    } 
    
    @Test
    public void testHashcode(){
        CityDTO cityDTO1 = getExampleCityDTO();
        int x = cityDTO1.hashCode();
    }

    @Test
    public void testToString(){
        CityDTO cityDTO1 = getExampleCityDTO();
        String word = cityDTO1.toString();
    }

    public CityDTO getExampleCityDTO(){
        return new CityDTO("hello", "hello", 0, 0);
    }

    public CityDTO getDifferentExampleCityDTO(){
        return new CityDTO("goodbye", "hello", 0, 0);
    }


}
