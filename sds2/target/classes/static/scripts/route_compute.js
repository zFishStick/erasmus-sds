let directionsService;
let directionsRenderer;
let routeSet = false;

function computeRoute(map, origin, destination, waypoints = [], travelMode = "DRIVING") {

    if (!origin || !destination) {
        console.error("Origin or destination missing");
        return;
    }

    const directionsWaypoints = waypoints.map(wp => ({
        location: { lat: wp.lat, lng: wp.lng },
        stopover: true
    }));

    if (routeSet && directionsRenderer) {
        directionsRenderer.setMap(null);
    }

    directionsService = new google.maps.DirectionsService();
    directionsRenderer = new google.maps.DirectionsRenderer({
        map: map,
        suppressMarkers: false
    });

    directionsService.route({
        origin: origin,
        destination: destination,
        waypoints: directionsWaypoints,
        travelMode: travelMode
    }, (result, status) => {
        if (status === "OK") {
            routeSet = true;
            directionsRenderer.setDirections(result);
        } else {
            console.error("Route error:", status);
        }
    });
}
