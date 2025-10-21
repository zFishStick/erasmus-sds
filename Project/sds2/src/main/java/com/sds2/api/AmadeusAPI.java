
// Following file contains API operations for Amadeus Travel API
package com.sds2.api;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.PointOfInterest;

public class AmadeusAPI {

    private Amadeus amadeus;

    public AmadeusAPI(String apiKey, String apiSecret) {
        this.amadeus = Amadeus.builder(apiKey, apiSecret).build();
    }

    public PointOfInterest[] getPointOfInterests(float lat, float lon) throws ResponseException {
        PointOfInterest[] pois = amadeus.referenceData.locations.pointsOfInterest.get(Params
            .with("latitude", lat)
            .and("longitude", lon));
        return pois;
    }
    
}