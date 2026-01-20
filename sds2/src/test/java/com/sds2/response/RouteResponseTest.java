package com.sds2.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

import com.sds2.classes.response.RouteResponse;

class RouteResponseTest {

    @Test
    void testRoutesGetterSetter() {
        RouteResponse resp = new RouteResponse();
        RouteResponse.Route r = new RouteResponse.Route();
        List<RouteResponse.Route> list = Arrays.asList(r);

        resp.setRoutes(list);
        assertSame(list, resp.getRoutes());
        assertSame(r, resp.getRoutes().get(0));
    }

    @Test
    void testRouteLegsAndProperties() {
        RouteResponse.RouteLeg leg = new RouteResponse.RouteLeg();
        leg.setDistanceMeters(1234);
        leg.setDuration("00:20:00");
        leg.setOrigin("Start");
        leg.setDestination("End");

        RouteResponse.Polyline poly = new RouteResponse.Polyline();
        poly.setEncodedPolyline("encoded123");
        leg.setPolyline(poly);

        RouteResponse.Route route = new RouteResponse.Route();
        route.setLegs(new RouteResponse.RouteLeg[] { leg });

        RouteResponse resp = new RouteResponse();
        resp.setRoutes(Arrays.asList(route));

        RouteResponse.RouteLeg actualLeg = resp.getRoutes().get(0).getLegs()[0];
        assertEquals(1234, actualLeg.getDistanceMeters());
        assertEquals("00:20:00", actualLeg.getDuration());
        assertEquals("Start", actualLeg.getOrigin());
        assertEquals("End", actualLeg.getDestination());
        assertSame(poly, actualLeg.getPolyline());
        assertEquals("encoded123", actualLeg.getPolyline().getEncodedPolyline());
    }

    @Test
    void testPolylineGetterSetter() {
        RouteResponse.Polyline p = new RouteResponse.Polyline();
        p.setEncodedPolyline("abc");
        assertEquals("abc", p.getEncodedPolyline());
    }
}