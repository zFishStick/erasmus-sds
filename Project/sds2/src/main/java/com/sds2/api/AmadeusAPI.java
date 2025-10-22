
// Following file contains API operations for Amadeus Travel API
package com.sds2.api;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.json.JSONArray;
import org.json.JSONObject;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Activity;
import com.sun.net.httpserver.HttpServer;

public class AmadeusAPI {

    private Amadeus amadeus;

    public void registerAmadeusEndpoints(HttpServer server) {
        createEndpoint(server, "/pois", "Amadeus API is operational."); // Point of Interests
        createEndpoint(server, "/activities", "Activities endpoint reached.");
        createEndpoint(server, "/flights", "Flights endpoint reached.");
    }

    private void createEndpoint(HttpServer server, String path, String message) {
        server.createContext("/amadeus" + path, exchange -> {
            try (InputStream is = exchange.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JSONObject jsonBody = new JSONObject(body);
                JSONArray pois;
                try {
                    pois = getPointOfInterests(
                        jsonBody.getFloat("latitude"),
                        jsonBody.getFloat("longitude")
                    );
                } catch (ResponseException e) {
                    String resp = new JSONObject().put("error", e.getMessage()).toString();
                    byte[] respBytes = resp.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(500, respBytes.length);
                    exchange.getResponseBody().write(respBytes);
                    exchange.getResponseBody().close();
                    return;
                }

                byte[] response = pois.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.length);
                exchange.getResponseBody().write(response);
                exchange.getResponseBody().close();

            }
        });
    }


    public AmadeusAPI(String apiKey, String apiSecret) throws ResponseException {
        this.amadeus = Amadeus.builder(apiKey, apiSecret).build();
    }

    public JSONArray getPointOfInterests(float lat, float lon) throws ResponseException {
        Activity[] pois = amadeus.shopping.activities.get(Params
            .with("latitude", lat)
            .and("longitude", lon)
            .and("radius", 20));

        JSONArray poiArray = new JSONArray();

        for (Activity act : pois) {
            JSONObject obj = new JSONObject();
            if (act.getGeoCode() != null) {
                obj.put("latitude", act.getGeoCode().getLatitude());
                obj.put("longitude", act.getGeoCode().getLongitude());
            }
            poiArray.put(obj);
        }

        return poiArray;
    }
    
}