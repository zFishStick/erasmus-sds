package com.sds2;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.resources.FlightDestination;

import io.github.cdimascio.dotenv.Dotenv;

public class ApiTest {

    @BeforeAll
    public static void init() {
        Dotenv dotenv = Dotenv.load();

        String apiKey = dotenv.get("AMADEUS_API_KEY");
        String apiSecret = dotenv.get("AMADEUS_API_SECRET");

        System.out.println("API Key: " + apiKey);
        System.out.println("API Secret: " + apiSecret);
    }

    @Test
    public void testFlies() {
        Dotenv dotenv = Dotenv.load();

        String apiKey = dotenv.get("AMADEUS_API_KEY");
        String apiSecret = dotenv.get("AMADEUS_API_SECRET");

        try{
        Amadeus amadeus = Amadeus.builder(apiKey, apiSecret).build();

        Params params = Params.with("origin", "MAD");

        FlightDestination[] flightDestinations = amadeus.shopping.flightDestinations.get(params);

        if (flightDestinations[0].getResponse().getStatusCode() != 200) {
            System.out.println("Wrong status code for Flight Inspiration Search: " + flightDestinations[0].getResponse().getStatusCode());
            System.exit(-1);
        }

        System.out.println(flightDestinations[0]);
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }

    }

}
