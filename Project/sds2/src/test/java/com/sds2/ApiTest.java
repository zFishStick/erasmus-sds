package com.sds2;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.amadeus.Amadeus;
import com.amadeus.resources.City;

import io.github.cdimascio.dotenv.Dotenv;

public class ApiTest {
    ApiTest apiTest = new ApiTest();

    @BeforeAll
    public static void init() {
        Dotenv dotenv = Dotenv.load();

        String apiKey = dotenv.get("AMADEUS_API_KEY");
        String apiSecret = dotenv.get("AMADEUS_API_SECRET");

        System.out.println("API Key: " + apiKey);
        System.out.println("API Secret: " + apiSecret);
    }

    @Test
    public City[] testGetCities() throws Exception {
        Dotenv dotenv = Dotenv.load();

        String apiKey = dotenv.get("AMADEUS_API_KEY");
        String apiSecret = dotenv.get("AMADEUS_API_SECRET");

        Amadeus amadeus = Amadeus.builder(apiKey, apiSecret).build();

        City[] cities = amadeus.referenceData.locations.cities.get(
            com.amadeus.Params
                .with("keyword", "Madrid")
                .and("subType", "CITY")
        );

        for (City city : cities) {
            System.out.println("City: " + city.getName() + ", Country: " + city.getAddress().getCountryCode());
        }

        return cities;
    }

}
